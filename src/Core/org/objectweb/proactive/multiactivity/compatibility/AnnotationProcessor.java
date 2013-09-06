/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.multiactivity.compatibility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineGraphBasedPriorities;
import org.objectweb.proactive.annotation.multiactivity.DefineRankBasedPriorities;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.DefineThreadConfig;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.Priority;
import org.objectweb.proactive.annotation.multiactivity.Set;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.annotation.multiactivity.PriorityOrder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.multiactivity.limits.ThreadManager;
import org.objectweb.proactive.multiactivity.limits.ThreadMap;
import org.objectweb.proactive.multiactivity.priority.PriorityGraph;
import org.objectweb.proactive.multiactivity.priority.PriorityRanking;
import org.objectweb.proactive.multiactivity.priority.PriorityMap;
import org.objectweb.proactive.multiactivity.priority.PriorityUtils;


/**
 * Reads and processes the multi-activity related annotations of a class and
 * produces two data structures that describe the compatibility of the methods
 * of this class. <br>
 * These data structures are:
 * <ul>
 * <li>group map -- this is a map that associates the names of the groups with
 * the actual {@link MethodGroup}s. It can be retrieved through the
 * {@link #getMethodGroups()} method.</li>
 * <li>method map -- this is a map that holds the group that each method belongs
 * to. It can be accessed through the {@link #getMethodMemberships()} method.</li>
 * </ul>
 * 
 * <br>
 * For information on the multi-active annotations, please refer to the
 * <code>org.objectweb.proactive.annotations.multiactivity</code> package.
 * 
 * @author The ProActive Team
 * 
 */
public class AnnotationProcessor {

	// text used to define the place and type of an error in the annotations
	protected static final String LOC_CLASS = "at class";
	protected static final String LOC_METHOD = "at method";
	protected static final String UNDEF_GROUP = "undefined group";
	protected static final String DUP_GROUP = "duplicate group definition";
	protected static final String UNDEF_METHOD = "unresolvable method name";
	protected static final String PRIORITY_CYCLE = "priority cycle detected" +
			": dependence from ";
	protected static final String AD_HOC_GROUP = "AD_HOC_";

	protected Logger logger = ProActiveLogger.getLogger(Loggers.MULTIACTIVITY);

	// Stores Group names -> group and Method names -> groups
	private CompatibilityMap compatibilityMap = new CompatibilityMap();

	// priority structures
	private PriorityGraph priorityGraph = new PriorityGraph();
	private PriorityRanking priorityRanking = new PriorityRanking();
	
	// group -> maximum number of threads used by methods of the group
	private ThreadMap threadMap = new ThreadMap();

	// class that is processed
	private Class<?> processedClass;

	// set of the method name inside a class -- used for error checking --
	// populated only on need
	private HashSet<String> classMethods;

	// list of errors
	protected List<String> errorMessages = new LinkedList<String>();

	public AnnotationProcessor(Class<?> c) {
		processedClass = c;

		// create the inheritance list of the class (in descending order of
		// "age")
		List<Class<?>> parents = new LinkedList<Class<?>>();
		parents.add(c);
		Class<?> p;
		while ((p = parents.get(0).getSuperclass()) != null) {
			parents.add(0, p);
		}

		Iterator<Class<?>> i = parents.iterator();

		// process group and rule definitions starting from the oldest class.
		while (i.hasNext()) {
			processAnnotationsAtClasses(i.next());
		}

		processAnnotationsAtMethods();
	}

	/**
	 * Reads and processes the following types of class-level annotations:
	 * <ul>
	 * <li>{@link DefineGroups} and {@link Group} -- these define the groups</li>
	 * <li>{@link DefineRules} and {@link Compatible} -- these define the rules
	 * that apply between them</li>
	 * </ul>
	 */
	protected void processAnnotationsAtClasses(Class<?> processedClass) {
		Annotation[] declaredAnns = processedClass.getDeclaredAnnotations();
		Annotation groupDefAnn = null;
		Annotation compDefAnn = null;
		Annotation priorityGraphDefAnn = null;
		Annotation priorityRankDefAnn = null;
		Annotation threadConfigDefAnn = null;
		int reservedThreads;
		int totalReservedThreads = 0;

		for (Annotation a : declaredAnns) {
			if (groupDefAnn == null
					&& a.annotationType().equals(DefineGroups.class)) {
				groupDefAnn = a;
			}

			if (compDefAnn == null
					&& a.annotationType().equals(DefineRules.class)) {
				compDefAnn = a;
			}

			if (priorityGraphDefAnn == null 
					&& a.annotationType().equals(
							DefineGraphBasedPriorities.class)) {
				priorityGraphDefAnn = a;
			}

			if (priorityRankDefAnn == null 
					&& a.annotationType().equals(
							DefineRankBasedPriorities.class)) {
				priorityRankDefAnn = a;
			}
			
			if (threadConfigDefAnn == null
					&& a.annotationType().equals(
							DefineThreadConfig.class)) {
				threadConfigDefAnn = a;
				threadMap.setConfiguredThroughAnnot();
			}

			if (compDefAnn != null && groupDefAnn != null
					&& priorityGraphDefAnn != null 
					&& priorityRankDefAnn != null && 
					threadConfigDefAnn != null) {
				break;
			}
		}

		// if there are groups
		if (groupDefAnn != null) {
			for (Group g : ((DefineGroups) groupDefAnn).value()) {
				if (!compatibilityMap.getGroups().containsKey(g.name())) {
					// Set the group compatibility
					MethodGroup mg =
							new MethodGroup(
									g.name(), g.selfCompatible(),
									g.parameter(), g.condition(), g.superPriority());
					compatibilityMap.getGroups().put(g.name(), mg);
					
					// Set the group thread limits
					reservedThreads = g.minThreads() < g.maxThreads() ? 
							g.minThreads() : g.maxThreads();
					threadMap.setThreadLimits(mg, reservedThreads, g.maxThreads());
					totalReservedThreads += reservedThreads;
					
				} else {
					addError(
							LOC_CLASS, processedClass.getCanonicalName(),
							DUP_GROUP, g.name());
				}
			}
			threadMap.setTotalReservedThreads(totalReservedThreads);
		}

		// if there are rules defined
		if (compDefAnn != null) {
			for (Compatible c : ((DefineRules) compDefAnn).value()) {
				String comparator = c.condition();
				for (String group : c.value()) {
					for (String other : c.value()) {
						if (compatibilityMap.getGroups().containsKey(group)
								&& compatibilityMap.getGroups().containsKey(other)
								&& !other.equals(group)) {
							compatibilityMap.getGroups().get(group).addCompatibleWith(
									compatibilityMap.getGroups().get(other));
							compatibilityMap.getGroups().get(group).setComparatorFor(
									other, comparator);
							compatibilityMap.getGroups().get(other).addCompatibleWith(
									compatibilityMap.getGroups().get(group));
							compatibilityMap.getGroups().get(other).setComparatorFor(
									group, comparator);

						} else {
							if (!compatibilityMap.getGroups().containsKey(group)) {
								addError(
										LOC_CLASS,
										processedClass.getCanonicalName(),
										UNDEF_GROUP, group);
							}
							if (!compatibilityMap.getGroups().containsKey(other)) {
								addError(
										LOC_CLASS,
										processedClass.getCanonicalName(),
										UNDEF_GROUP, other);
							}
						}
					}
				}

			}
		}

		// if there are graph based priorities defined
		if (priorityGraphDefAnn != null) {

			List<MethodGroup> predecessors = new LinkedList<MethodGroup>();
			List<MethodGroup> nextPredecessors = new LinkedList<MethodGroup>();

			for (PriorityOrder priorityOrder : ((DefineGraphBasedPriorities) priorityGraphDefAnn).value()) {
				for (Set priority : priorityOrder.value()) {
					for (String groupName : priority.groupNames()) {
						// Get the group object associated with the group name
						MethodGroup group = this.compatibilityMap.getGroups().get(groupName);
						if (group != null) {
							if (predecessors.isEmpty()) {
								priorityGraph.insert(group, null);
							}
							else {
								for(MethodGroup predecessor : predecessors) {
									priorityGraph.insert(group, predecessor);
									if (priorityGraph.containsCycle()) {
										addError(
												LOC_CLASS,
												processedClass.getCanonicalName(),
												PRIORITY_CYCLE, predecessor.name + " to " + group.name + " removed");
										priorityGraph.suppress(group, predecessor);
									}
								}
							}
						}
						nextPredecessors.add(group);
					}
					predecessors.clear();
					predecessors.addAll(nextPredecessors);
					nextPredecessors.clear();
				}
				predecessors.clear();
			}
			if (PriorityUtils.LOG_ENABLED) {
				PriorityUtils.logMessage(priorityGraph.toString());
			}
		}

		// if there are rank based priorities defined
		if (priorityRankDefAnn != null) {

			int priorityLevel;

			for (Priority priority : ((DefineRankBasedPriorities) priorityRankDefAnn).value()) {

				priorityLevel = priority.level();

				for (String groupName : priority.groupNames()) {
					MethodGroup group = this.compatibilityMap.getGroups().get(groupName);
					if (group != null) {
						priorityRanking.insert(priorityLevel, group);
					}
				}
			}
		}
		
		// if there are configuration for threads defined
		if (threadConfigDefAnn != null) {
			DefineThreadConfig threadConfig = ((DefineThreadConfig) threadConfigDefAnn);
			int poolSize = totalReservedThreads >= threadConfig.threadPoolSize() ? 
					totalReservedThreads + ThreadManager.THREAD_POOL_MARGIN : threadConfig.threadPoolSize();
			threadMap.configure(poolSize, threadConfig.hardLimit(),
					threadConfig.hostReentrant());
			if (PriorityUtils.LOG_ENABLED) {
				PriorityUtils.logMessage(
						"Configuration of threads: thread pool size = " + poolSize + "" +
								", hard limit = " + threadConfig.hardLimit() + "" +
										", host reentrant = " + threadConfig.hostReentrant());
			}
		}
	}

	/*
	 * Reads and processes the following method-level annotations: <ul> <li>{@link Reads} -- the
	 * variables that are just read by the method</li> <li>{@link Modifies} -- the variables that
	 * are written by the method</li> </ul>
	 */
	/*
	 * protected void processReadModify() { //map method names to variables HashMap<String,
	 * HashSet<String>> reads = new HashMap<String, HashSet<String>>(); HashMap<String,
	 * HashSet<String>> modifs = new HashMap<String, HashSet<String>>();
	 * 
	 * //map variables to referencing method names HashMap<String, HashSet<String>> readVars = new
	 * HashMap<String, HashSet<String>>(); HashMap<String, HashSet<String>> modifVars = new
	 * HashMap<String, HashSet<String>>();
	 * 
	 * //extract all variable-related info for (Method method : processedClass.getMethods()) {
	 * boolean selfCompatible = true; String methodName = method.getName();
	 * 
	 * //first the reads Reads read = method.getAnnotation(Reads.class); if (read!=null &&
	 * read.value().length>0) { for (String var : read.value()) {
	 * 
	 * if (!classHasVariable(var)) { addError(LOC_METHOD, methodName, REF_VARIABLE, var); }
	 * 
	 * if (reads.get(methodName)==null) { reads.put(methodName, new HashSet<String>()); }
	 * reads.get(methodName).add(var);
	 * 
	 * if (readVars.get(var)==null) { readVars.put(var, new HashSet<String>()); }
	 * readVars.get(var).add(methodName); } }
	 * 
	 * //read the modifies annotations Modifies modify = method.getAnnotation(Modifies.class); if
	 * (modify!=null && modify.value().length>0) { //if the method modifies something it is not
	 * selfCompatible; //[!] unless defined otherwise with compatibleWith annotation selfCompatible
	 * = false;
	 * 
	 * for (String var : modify.value()) { if (!classHasVariable(var)) { addError(LOC_METHOD,
	 * methodName, REF_VARIABLE, var); }
	 * 
	 * if (modifs.get(methodName)==null) { modifs.put(methodName, new HashSet<String>()); }
	 * modifs.get(methodName).add(var);
	 * 
	 * if (modifVars.get(var)==null) { modifVars.put(var, new HashSet<String>()); }
	 * modifVars.get(var).add(methodName); }
	 * 
	 * }
	 * 
	 * // if no annotations, get out! if (read==null && modify==null) { break; }
	 * 
	 * //create a group for the method if needed, or extend the already existing one MethodGroup
	 * newGroup; if (methods.containsKey(methodName) &&
	 * methods.get(methodName).name.startsWith(AD_HOC_GROUP)){ //if the method already has an
	 * "explicit" group, modify it newGroup = methods.get(methodName); } else { newGroup = new
	 * MethodGroup(groups.get(method), AD_HOC_GROUP+methodName, selfCompatible);
	 * methods.put(methodName, newGroup); groups.put(newGroup.name, newGroup); }
	 * 
	 * }
	 * 
	 * //go through the annotated methods HashSet<String> anntMethods = new HashSet<String>();
	 * anntMethods.addAll(modifs.keySet()); anntMethods.addAll(reads.keySet());
	 * 
	 * for (String method : anntMethods) { for (String other : anntMethods) { //flag for
	 * compatibility. //A is compatible with B if // - A does not modify what B modifies // - B does
	 * not read what A modifies // - A does not read what B modifies boolean areOk = true; if
	 * (modifs.containsKey(method)) { for (String mVar : modifs.get(method)) { if
	 * ((modifVars.containsKey(mVar) && modifVars.get(mVar).contains(other)) ||
	 * (readVars.containsKey(mVar) && readVars.get(mVar).contains(other))) { areOk=false; break; } }
	 * } if (areOk && reads.containsKey(method)) { for (String rVar : reads.get(method)) { if
	 * (modifVars.containsKey(rVar) && modifVars.get(rVar).contains(other)) { areOk=false; break; }
	 * } }
	 * 
	 * if (areOk) { methods.get(method).addCompatibleWith(methods.get(other)); } } } }
	 */

	/**
	 * Reads and processes the following method-level annotations:
	 * <ul>
	 * <li>{@link MemberOf} -- the group the method belongs to</li>
	 * <li>{@link Compatible} -- the additional methods this method is
	 * compatible with</li>
	 * </ul>
	 */
	protected void processAnnotationsAtMethods() {
		// HashMap<String, HashSet<String>> compMap =
		// new HashMap<String, HashSet<String>>();

		// go through each public method of a class
		for (Method method : processedClass.getMethods()) {

			// check what group is it part of
			MemberOf group = method.getAnnotation(MemberOf.class);
			if (group != null) {
				MethodGroup mg = compatibilityMap.getGroups().get(group.value());

				String methodSignature = method.toString();
				compatibilityMap.getMembership().put(
						methodSignature.substring(methodSignature.indexOf(method.getName())),
						mg);

				if (mg == null) {
					addError(
							LOC_METHOD, method.toString(), UNDEF_GROUP,
							group.value());
				}
			}

			/*
			 * //other compatible declarations -- put them into a map Compatible comp =
			 * method.getAnnotation(Compatible.class); if (comp!=null) { HashSet<String> comps = new
			 * HashSet<String>(); for (String other : comp.value()) { comps.add(other);
			 * 
			 * if (!classHasMethod(other)) { addError(LOC_METHOD, method.toString(), UNDEF_GROUP,
			 * other); } } compMap.put(method.toString(), comps); }
			 */
		}

		/*
		 * //go through methods that declared compatibilities for (String method : compMap.keySet())
		 * { boolean selfCompatible = compMap.get(method).contains(method) ||
		 * (groups.get(method)!=null ? groups.get(method).isSelfCompatible() : false);
		 * 
		 * //create a group for this method -- maybe extend its already existing group MethodGroup
		 * newGroup; if (methods.containsKey(method) &&
		 * methods.get(method).name.startsWith(AD_HOC_GROUP)){ newGroup = methods.get(method); }
		 * else { newGroup = new MethodGroup(groups.get(method), AD_HOC_GROUP+method,
		 * selfCompatible); methods.put(method, newGroup); groups.put(newGroup.name, newGroup); } }
		 * 
		 * //set compatibilities based on the method-level compatible annotations for (String method
		 * : compMap.keySet()) { for (String other : compMap.get(method)) { if
		 * (compMap.containsKey(other) && compMap.get(other).contains(method)) {
		 * methods.get(method).addCompatibleWith(methods.get(other));
		 * methods.get(other).addCompatibleWith(methods.get(method)); } else { addError(LOC_METHOD,
		 * method, UNDEF_METHOD, other); } } }
		 */
	}

	/**
	 * Adds an error to the error map (which keep
	 * 
	 * @param locationType
	 * @param location
	 * @param problemType
	 * @param problemName
	 */
	private void addError(String locationType, String location,
			String problemType, String problemName) {
		String msg;

		if (!locationType.equals(LOC_CLASS)) {
			msg =
					"In '" + processedClass.getName() + "' " + locationType
					+ " '" + location + "' " + problemType + " '"
					+ problemName + "'";
			logger.error(msg);
			errorMessages.add(msg);
		} else {
			msg =
					"In '" + location + "' " + problemType + " '" + problemName
					+ "'";
			logger.error(msg);
			errorMessages.add(msg);
		}
	}

	/**
	 * Returns the list of error messages thrown while processing annotations.
	 * 
	 * @return
	 */
	public Collection<String> getErrorMessages() {
		return errorMessages;
	}

	/**
	 * Returns true if the annotations contain references (group names, method
	 * names) that are not defined in the class, or if some groups were
	 * redefined.
	 * 
	 * @return
	 */
	public boolean hasErrors() {
		return errorMessages.size() > 0;
	}

	public CompatibilityMap getCompatibilityMap() {
		return this.compatibilityMap;
	}
	
	/**
	 * Returns a map that maps the group names to the method groups.
	 * 
	 * @return
	 */
	/*public Map<String, MethodGroup> getMethodGroups() {
		return compatibilityMap.getGroups();
	}*/

	/**
	 * Returns a map that pairs each method name with a method group.
	 * 
	 * @return
	 */
	/*public Map<String, MethodGroup> getMethodMemberships() {
		return compatibilityMap.getMembership();
	}*/

	/*
	 * Returns true if the processed class has a variable named like the parameter.
	 * 
	 * @param ref variable name
	 * 
	 * @return
	 */
	/*
	 * private boolean classHasVariable(String what) { if (classVariables==null) { classVariables =
	 * new HashSet<String>(); Field[] vars = processedClass.getDeclaredFields(); for (int i=0;
	 * i<vars.length; i++) { classVariables.add(vars[i].getName()); } } return
	 * classVariables.contains(what); }
	 */

	@SuppressWarnings("unused")
	private boolean classHasMethod(String what) {
		if (classMethods == null) {
			classMethods = new HashSet<String>();
			Method[] meths = processedClass.getMethods();
			for (int i = 0; i < meths.length; i++) {
				classMethods.add(meths[i].getName());
			}
		}
		return classMethods.contains(what);
	}

	public PriorityMap getPriorityMap() {
		PriorityMap structure = null;
		switch (PriorityMap.currentStructure) {
		case RANK_BASED:
			structure = this.priorityRanking;
			break;
		case GRAPH_BASED:
			structure = this.priorityGraph;
			break;
		}
		return structure;
	}
	
	public ThreadMap getThreadMap() {
		return this.threadMap;
	}

}
