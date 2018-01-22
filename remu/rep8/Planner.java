import java.io.File;
import java.util.*;

public class Planner {
	Vector operators;
	Vector plan;
	//Vector subGoals = new Vector();
	PlannerGUI pgui;
	ArrayList<String> process = new ArrayList<String>();
	ArrayList<Integer> checkGoalCounts = new ArrayList<Integer>();
	ArrayList<ArrayList<Operator>> pairOperators = new ArrayList<ArrayList<Operator>>();
	//String tempSubGoal = "";

	public static void main(String argv[]) {
		(new Planner()).start();
	}

	Planner() {
		checkGoalCounts.add(0);
	}

	Planner(PlannerGUI pgui) {
		checkGoalCounts.add(0);
		this.pgui = pgui;
	}

	public void start() {
		initOperators();
		Vector goalList = initGoalList();
		Vector initialState = initInitialState();

		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		ArrayList<String> subGoalList = new ArrayList<String>();
		planning(goalList, initialState, theBinding, null, true, subGoalList);

		System.out.println("***** This is a plan! *****");
		for (int i = 0; i < plan.size(); i++) {
			Operator op = (Operator) plan.elementAt(i);
			System.out.println((op.instantiate(theBinding)).name);
		}

	}

	public void startWithGUI() {
		initOperators();
		Vector goalList = initGoalList(pgui);
		Vector initialState = initInitialState(pgui);
		System.out.println(initialState);
		Hashtable theBinding = new Hashtable();
		plan = new Vector();
		ArrayList<String> subGoalList = new ArrayList<String>();
		planning(goalList, initialState, theBinding, null, true,  subGoalList);
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		Operator op1, op2;
		String s1, s2;
		for (int i = 0; i < plan.size(); i++) {
			process.add(((Operator) plan.elementAt(i)).instantiate(theBinding).name);
			if (i != 0) {
				op1 = (Operator) plan.elementAt(i - 1);
				op2 = (Operator) plan.elementAt(i);
				s1 = (op1.instantiate(theBinding)).name.replace(' ', '_') + "_"
						+ i;
				s2 = (op2.instantiate(theBinding)).name.replace(' ', '_') + "_"
						+ (i + 1);
				gv.addln(s1 + " -> " + s2 + ";");
			}
		}
		String type = "png";
		String repesentationType = "dot";
		pgui.counter++;
		File out = new File("tmp/simple"+ pgui.counter + "." + type);
		//gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type, repesentationType), out);
	}

	boolean planning(Vector theGoalList,
					 Vector theCurrentState,
					 Hashtable theBinding,
					 Operator nextOperator,
					 boolean isFirstStep,
					 ArrayList<String> subGoalList) {
		System.out.println("*** GOALS ***" + theGoalList);
		System.out.println("*** STATE ***" + theCurrentState);
		System.out.println("*** BIND ***" + theBinding);
		int countIndex = checkGoalCounts.size() - 1;
		if (theGoalList.size() == checkGoalCounts.get(countIndex))
		{
			checkGoalCounts.remove(checkGoalCounts.size() - 1);
			System.out.println(checkGoalCounts);
			return true;
		} else {
			// サブゴールの先頭を取り出し
			String aGoal = (String) theGoalList.elementAt(0);
			theCurrentState = alignStateList(aGoal, theCurrentState);

			// おそらく check point. ゴール状態を満たすまでに探索したオペレータのインデックスをtepPointから更新しつつ保持
			int cPoint = 0;
			while (cPoint < operators.size()) {
				System.out.println("cPoint:" + cPoint);

				// Store original binding
				Hashtable orgBinding = new Hashtable();
				for (Enumeration e = theBinding.keys(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					String value = (String) theBinding.get(key);
					orgBinding.put(key, value);
				}

				// Store original state
				Vector orgState = new Vector();
				for (int i = 0; i < theCurrentState.size(); i++) {
					orgState.addElement(theCurrentState.elementAt(i));
				}

				int tmpPoint = planningAGoal(theGoalList, theCurrentState, theBinding, cPoint, nextOperator, isFirstStep, subGoalList);

				// オペレータのインデックス+1 (そもそも現状にマッチングしてたら0)
				System.out.println("tmpPoint: " + tmpPoint);

				if (tmpPoint != -1) {
					String tempGoal = (String)theGoalList.remove(0);
					if(subGoalList.size() == 0)
						theGoalList.add(theGoalList.size(), tempGoal);
					else
					{
						theGoalList.add(0, tempGoal);
						subGoalList.remove(0);
					}
					System.out.println(theCurrentState);
					if (planning(theGoalList, theCurrentState, theBinding, nextOperator, false, subGoalList)) {
						System.out.println("Success !");
						return true;
					} else {
						cPoint = tmpPoint;
						// System.out.println("Fail::"+cPoint);
						theGoalList.insertElementAt(aGoal, 0);

						theBinding.clear();
						for (Enumeration e = orgBinding.keys(); e
								.hasMoreElements();) {
							String key = (String) e.nextElement();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int i = 0; i < orgState.size(); i++) {
							theCurrentState.addElement(orgState.elementAt(i));
						}
					}
				} else {
					theBinding.clear();
					for (Enumeration e = orgBinding.keys(); e.hasMoreElements();) {
						String key = (String) e.nextElement();
						String value = (String) orgBinding.get(key);
						theBinding.put(key, value);
					}
					theCurrentState.removeAllElements();
					for (int i = 0; i < orgState.size(); i++) {
						theCurrentState.addElement(orgState.elementAt(i));
					}
					return false;
				}
			}
			return false;
		}
	}

	private int planningAGoal(Vector theGoalList,
							  Vector theCurrentState,
							  Hashtable theBinding,
							  int cPoint,
							  Operator nextOperator,
							  boolean isFirstStep,
							  ArrayList<String> subGoalList) {
		String theGoal = (String)theGoalList.elementAt(0);
		System.out.println("**" + theGoal);
		int size = theCurrentState.size();
		for (int i = 0; i < size; i++) {
			String aState = (String) theCurrentState.elementAt(i);
			// 現状にマッチングする状態が来たら返す
			int tempUniqueNum = uniqueNum;
			boolean isRelatedNextOperator = false;
			if(nextOperator != null){
				isRelatedNextOperator = nextOperator.getIsRelatedNextOperator();
			}
			if ((new Unifier()).unify(theGoal, aState, theBinding, isRelatedNextOperator)) {
				int index = checkGoalCounts.size() - 1;
				checkGoalCounts.set(index, checkGoalCounts.get(index) + 1);
				//tempSubGoal = theGoal;
				return 0;
			}
		}

		int index = checkGoalCounts.size() - 1;
		checkGoalCounts.set(index, 1);

		/* ***************** 乱数の個所 ****************************** */

		/*
		int randInt = Math.abs(rand.nextInt()) % operators.size();
		Operator op = (Operator) operators.elementAt(randInt);
		operators.removeElementAt(randInt);
		operators.addElement(op);
		*/

		/* *********************************************************** */

		/* 対になるオペレータの優先順位を上げる */
		// どっちかっていうと置くほうだけ変数束縛が対になるオペレータと同じ場合を考えたい
		if(nextOperator != null){
			Vector pairs = nextOperator.GetPairedOperatorList();
			for(int i = 0; i < pairs.size(); ++i)
			{
				Operator pair = (Operator) pairs.get(i);
				operators.remove(pair);
				// 先頭に持ってく
				operators.add(0, pair);
			}
		}

		StringTokenizer tokens = new StringTokenizer(theGoal);
		String firstObject = tokens.nextToken();

		if(subGoalList.size() != 0)
		{
			theGoal = subGoalList.get(0);
			System.out.println("***" + theGoal);
		}
		else if(firstObject.equals("ontable") || tokens.nextToken().equals("on"))
		{
			theGoal = makeSubGoal(theGoal, theGoalList, theCurrentState, subGoalList);
			System.out.println("***" + theGoal);
		}

		for (int i = cPoint; i < operators.size(); i++) {
			Operator targetOperator = (Operator) operators.elementAt(i);
			Operator anOperator = rename(targetOperator);
			//System.out.println("target: " + targetOperator);

			// 現在のCurrent state, Binding, planをbackup
			Hashtable orgBinding = new Hashtable();
			for (Enumeration e = theBinding.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) theBinding.get(key);
				orgBinding.put(key, value);
			}
			Vector orgState = new Vector();
			for (int j = 0; j < theCurrentState.size(); j++) {
				orgState.addElement(theCurrentState.elementAt(j));
			}
			Vector orgPlan = new Vector();
			for (int j = 0; j < plan.size(); j++) {
				orgPlan.addElement(plan.elementAt(j));
			}

			// 条件が満たされたとき、追加される状態を格納
			Vector addList = (Vector) anOperator.getAddList();

			for (int j = 0; j < addList.size(); j++) {
				int tempUniqueNum = uniqueNum;
				boolean isRelatedNextOperator = false;
				if(nextOperator != null){
					isRelatedNextOperator = nextOperator.getIsRelatedNextOperator();
				}
				if ((new Unifier()).unify(theGoal,
						(String) addList.elementAt(j), theBinding, isRelatedNextOperator)) {
					// 具体化し、あらたなゴールを生成
					Operator newOperator = anOperator.instantiate(theBinding);
					Vector newGoals = (Vector) newOperator.getIfList();
					checkGoalCounts.add(0);
					ArrayList<String> nextSubGoalList = new ArrayList<String>();
					System.out.println(checkGoalCounts);

					// 再帰呼び出し
					if (planning(newGoals, theCurrentState, theBinding, targetOperator, false, nextSubGoalList)) {
						newOperator = newOperator.instantiate(theBinding);
						System.out.println(newOperator.name);
						plan.addElement(newOperator);
						theCurrentState = newOperator
								.applyState(theCurrentState);
						//int index = checkGoalCounts.size() - 1;
						//checkGoalCounts.set(index, 1);
						return i + 1;
					} else {
						// 失敗したら元に戻す．
						theBinding.clear();
						for (Enumeration e = orgBinding.keys(); e
								.hasMoreElements();) {
							String key = (String) e.nextElement();
							String value = (String) orgBinding.get(key);
							theBinding.put(key, value);
						}
						theCurrentState.removeAllElements();
						for (int k = 0; k < orgState.size(); k++) {
							theCurrentState.addElement(orgState.elementAt(k));
						}
						plan.removeAllElements();
						for (int k = 0; k < orgPlan.size(); k++) {
							plan.addElement(orgPlan.elementAt(k));
						}
					}
				}
			}
		}
		return -1;
	}

	int uniqueNum = 0;

	// 変数を区別したうえで、Operatorを生成
	private Operator rename(Operator theOperator) {
		Operator newOperator = theOperator.getRenamedOperator(uniqueNum);
		uniqueNum = uniqueNum + 1;
		return newOperator;
	}

	private String makeSubGoal(String theGoal, Vector goalList, Vector stateList, ArrayList<String> subGoalList)
	{
		//stateList内にはtheGoalはないと仮定
		String goal = theGoal;
		StringTokenizer tokens = new StringTokenizer(goal);
		String firstObject = tokens.nextToken();
		String lastObject;

		if(firstObject.equals("ontable") || tokens.nextToken().equals("on"))
		{
			lastObject = tokens.nextToken();
		}
		else
		{
			return goal;
		}

		if(!stateList.contains("clear " + lastObject))
		{
			for(int i = 0; i < stateList.size(); ++i)
			{
				String state = (String)stateList.get(i);
				if(state.indexOf("on " + lastObject) != -1)
				{
					StringTokenizer stateTokens = new StringTokenizer(state);
					String obj = stateTokens.nextToken();
					goal = "ontable " + obj;
					lastObject = obj;
					subGoalList.add(0, goal);
					i = -1;
				}
			}
		}

		if(!firstObject.equals("ontable") && !stateList.contains("clear " + firstObject))
		{
			for(int i = 0; i < stateList.size(); ++i)
			{
				String state = (String)stateList.get(i);
				if(state.indexOf("on " + firstObject) != -1)
				{
					StringTokenizer stateTokens = new StringTokenizer(state);
					String obj = stateTokens.nextToken();
					goal = "ontable " + obj;
					firstObject = obj;
					subGoalList.add(0, goal);
					i = -1;
				}
			}
		}

		if(subGoalList.size() != 0)
			return subGoalList.get(0);

		return goal;
	}

	// ゴールリストを都合のいいように編集
	private Vector alignGoalList(Vector goalList)
	{
		Vector newGoalList = new Vector();
		ArrayList<ArrayList<String>> allObjects = new ArrayList<ArrayList<String>>();

		for(int i = 0; i < goalList.size(); ++i)
		{
			StringTokenizer tokens1 = new StringTokenizer((String)goalList.get(i));
			String firstObject1 = "";
			String lastObject1 = "";
			String goal1 = (String)goalList.get(i);
			
			if(newGoalList.size() == 0)
			{
				newGoalList.add(goal1);
				continue;
			}

			if(tokens1.hasMoreTokens())
				firstObject1 = tokens1.nextToken();
			if(firstObject1.equals("clear") || firstObject1.equals("ontable"))
			{
				goalList.set(i, "ontable " + tokens1.nextToken());
				newGoalList.add(goal1);
				continue;
			}
			if(tokens1.hasMoreTokens() && !tokens1.nextToken().equals("on"))
				continue;
			if(tokens1.hasMoreTokens())
			{
				lastObject1 = tokens1.nextToken();
			}
			else
				continue;

			for(int j = 0; j < newGoalList.size(); ++j)
			{
				String goal2 = (String)newGoalList.get(j);
				StringTokenizer tokens2 = new StringTokenizer(goal2);
				String firstObject2 = tokens2.nextToken();
				String lastObject2;
				
				if(tokens2.hasMoreElements() && tokens2.nextElement().equals("on"))
				{
					lastObject2 = tokens2.nextToken();
				}
				else
				{
					continue;
				}

				if(firstObject1.equals(lastObject2))
				{
					newGoalList.add(j, goal1);
					break;
				}
				else if(lastObject1.equals(firstObject2))
				{
					newGoalList.add(j+1, goal1);
				}
			}
		}

		String temp = "";
		
		for(int i = 0, j = 0; i < goalList.size(); ++i)
		{
			String lowerObj = "";
			String topObj = "";

			StringTokenizer tokens = new StringTokenizer((String)goalList.get(i));
			topObj = tokens.nextToken();
			if(!tokens.hasMoreTokens() || !tokens.nextToken().equals("on"))
			{
				continue;
			}
			lowerObj = tokens.nextToken();

			String goal = "ontable " + lowerObj;

			if(!temp.equals(lowerObj) && !goalList.contains(goal))
			{
				goalList.add(i, goal);
			}

			temp = topObj;
		}

		return newGoalList;

		/*
		for(int index = 0; index < goalList.size(); ++index){
			ArrayList<String> objects = new ArrayList<String>();
			boolean isOnState = false;
			StringTokenizer tokenizer = new StringTokenizer((String)goalList.get(index));
			String firstObject = "";
			String lastObject =  "";

			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				if(!token.equals("on")){
					objects.add(token);
					if(firstObject.equals(""))
						firstObject = token;
					else if(lastObject.equals(""))
						lastObject = token;
				}
				else{
					isOnState = true;
				}
			}

			int insertIndex = 0;

			for(ArrayList<String> list : allObjects)
			{

			}

			if(allObjects.contains(firstObject)){
				insertIndex = allObjects.indexOf(firstObject);
				allObjects.remove(insertIndex);
			}

			if(isOnState){
				allObjects.addAll(insertIndex, objects);
			}
			else{
				newGoalList.add((String)goalList.get(index));
			}
		}

		for(int index = allObjects.size() - 1; index > 0; --index){
			String goal = allObjects.get(index - 1) + " on " + allObjects.get(index);
			newGoalList.add(goal);
		}

		return newGoalList;
		*/
	}

	private Vector alignStateList(String goal, Vector stateList)
	{
		StringTokenizer tokens = new StringTokenizer(goal);
		Vector priorityStateList = new Vector();

		while(tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();

			if(!token.equals("on") && !tokens.equals("ontable"))
			{
				for(int index = 0, delete = 0; index - delete < stateList.size(); ++index)
				{
					String state = (String)stateList.get(index - delete);
					StringTokenizer stateTokens = new StringTokenizer(state);
					while(stateTokens.hasMoreTokens())
					{
						if(stateTokens.nextToken().equals(token))
						{
							stateList.remove(state);
							priorityStateList.add(state);
							++delete;
							break;
						}
					}
				}
			}
		}

		stateList.addAll(0, priorityStateList);
		return stateList;
	}

	private Vector initGoalList() {
		Vector goalList = new Vector();

		goalList.addElement("A on B");
		goalList.addElement("B on C");
		goalList.addElement("C on D");
		//goalList.addElement("B on C");
		//goalList.addElement("A on B");
		//goalList.addElement("D on A");
		//goalList.addElement("C on B");

		/*
		goalList.addElement("D on E");
		goalList.addElement("A on B");
		goalList.addElement("C on D");
		*/
		Vector newGoalList = alignGoalList(goalList);
		System.out.println(newGoalList);
		return newGoalList;
	}

	private Vector initGoalList(PlannerGUI pgui) {
		Vector goalList = new Vector();
		 String[] gList = pgui.goalTextArea.getText().split("\n");
		 for(int i=0; i<gList.length; i++){
			 goalList.addElement(gList[i]);
		 }
		 return alignGoalList(goalList);
	}

	private Vector initInitialState() {
		Vector initialState = new Vector();
		initialState.addElement("clear A");
		initialState.addElement("clear B");
		initialState.addElement("clear C");
		initialState.addElement("clear D");


		initialState.addElement("ontable A");
		initialState.addElement("ontable B");
		initialState.addElement("ontable C");
		initialState.addElement("ontable D");
		initialState.addElement("handEmpty");
		return initialState;
	}

	private Vector initInitialState(PlannerGUI pgui) {
		Vector initialState = new Vector();
		 String[] iList = pgui.initialTextArea.getText().split("\n");
		 for(int i=0; i<iList.length; i++){
			 initialState.addElement(iList[i]);
		 }
		 return initialState;
	}

	private void initOperators() {
		operators = new Vector();


		// OPERATOR 1
		// / NAME
		String name1 = new String("Place ?x on ?y");
		// / IF
		Vector ifList1 = new Vector();
		ifList1.addElement(new String("clear ?y"));
		ifList1.addElement(new String("holding ?x"));
		// / ADD-LIST
		Vector addList1 = new Vector();
		addList1.addElement(new String("?x on ?y"));
		addList1.addElement(new String("clear ?x"));
		addList1.addElement(new String("handEmpty"));
		// / DELETE-LIST
		Vector deleteList1 = new Vector();
		deleteList1.addElement(new String("clear ?y"));
		deleteList1.addElement(new String("holding ?x"));
		Operator operator1 = new Operator(name1, ifList1, addList1, deleteList1, false);
		operators.addElement(operator1);

		// OPERATOR 2
		// / NAME
		String name2 = new String("remove ?x from on top ?y");
		// / IF
		Vector ifList2 = new Vector();
		ifList2.addElement(new String("?x on ?y"));
		ifList2.addElement(new String("clear ?x"));
		ifList2.addElement(new String("handEmpty"));
		// / ADD-LIST
		Vector addList2 = new Vector();
		addList2.addElement(new String("clear ?y"));
		addList2.addElement(new String("holding ?x"));
		// / DELETE-LIST
		Vector deleteList2 = new Vector();
		deleteList2.addElement(new String("?x on ?y"));
		deleteList2.addElement(new String("clear ?x"));
		deleteList2.addElement(new String("handEmpty"));
		Operator operator2 = new Operator(name2, ifList2, addList2, deleteList2, true);
		operators.addElement(operator2);

		// OPERATOR 3
		// / NAME
		String name3 = new String("pick up ?x from the table");
		// / IF
		Vector ifList3 = new Vector();
		ifList3.addElement(new String("ontable ?x"));
		ifList3.addElement(new String("clear ?x"));
		ifList3.addElement(new String("handEmpty"));
		// / ADD-LIST
		Vector addList3 = new Vector();
		addList3.addElement(new String("holding ?x"));
		// / DELETE-LIST
		Vector deleteList3 = new Vector();
		deleteList3.addElement(new String("ontable ?x"));
		deleteList3.addElement(new String("clear ?x"));
		deleteList3.addElement(new String("handEmpty"));
		Operator operator3 = new Operator(name3, ifList3, addList3, deleteList3, true);
		operators.addElement(operator3);

		// OPERATOR 4
		// / NAME
		String name4 = new String("put ?x down on the table");
		// / IF
		Vector ifList4 = new Vector();
		ifList4.addElement(new String("holding ?x"));
		// / ADD-LIST
		Vector addList4 = new Vector();
		addList4.addElement(new String("ontable ?x"));
		addList4.addElement(new String("clear ?x"));
		addList4.addElement(new String("handEmpty"));
		// / DELETE-LIST
		Vector deleteList4 = new Vector();
		deleteList4.addElement(new String("holding ?x"));
		Operator operator4 = new Operator(name4, ifList4, addList4, deleteList4, false);
		operators.addElement(operator4);

		ArrayList<Operator> tempPairs = new ArrayList<Operator>();

		tempPairs.add(operator3);
		tempPairs.add(operator4);
		pairOperators.add(new ArrayList<Operator>(tempPairs));
		tempPairs.clear();

		tempPairs.add(operator1);
		tempPairs.add(operator2);
		pairOperators.add(new ArrayList<Operator>(tempPairs));
		tempPairs.clear();


		// 対になるオペレータを登録
		operator1.addPairedOperator(operator3);
		operator1.addPairedOperator(operator4);

		operator2.addPairedOperator(operator3);
		operator2.addPairedOperator(operator4);

		operator3.addPairedOperator(operator1);
		operator3.addPairedOperator(operator2);

		operator4.addPairedOperator(operator1);
		operator4.addPairedOperator(operator2);
	}
}

class Operator {
	String name;
	Vector ifList;
	Vector addList;
	Vector deleteList;
	Vector pairedOperatorList;
	boolean isRelatedNextOperator;

	Operator(String theName,
			 Vector theIfList,
			 Vector theAddList,
			 Vector theDeleteList,
			 boolean theFlag) {
		name = theName;
		ifList = theIfList;
		addList = theAddList;
		deleteList = theDeleteList;
		pairedOperatorList = new Vector();
		isRelatedNextOperator = theFlag;
	}

	public Vector getAddList() {
		return addList;
	}

	public Vector getDeleteList() {
		return deleteList;
	}

	public Vector getIfList() {
		return ifList;
	}

	public Vector GetPairedOperatorList(){
		return pairedOperatorList;
	}

	public void addPairedOperator(Operator operator){
		pairedOperatorList.add(operator);
	}

	public boolean checkPairedOperator(Operator operator){
		return pairedOperatorList.contains(operator);
	}

	public boolean getIsRelatedNextOperator(){
		return isRelatedNextOperator;
	}

	public String toString() {
		String result = "NAME: " + name + "\n" + "IF :" + ifList + "\n"
				+ "ADD:" + addList + "\n" + "DELETE:" + deleteList;
		return result;
	}

	public Vector applyState(Vector theState) {
		for (int i = 0; i < addList.size(); i++) {
			theState.addElement(addList.elementAt(i));
		}
		for (int i = 0; i < deleteList.size(); i++) {
			theState.removeElement(deleteList.elementAt(i));
		}
		return theState;
	}

	// 現状の選ばれたIFリストなどの内容をOperatorで返す
	public Operator getRenamedOperator(int uniqueNum) {
		Vector vars = new Vector();
		// IfListの変数を集める
		for (int i = 0; i < ifList.size(); i++) {
			String anIf = (String) ifList.elementAt(i);
			vars = getVars(anIf, vars);
		}
		// addListの変数を集める
		for (int i = 0; i < addList.size(); i++) {
			String anAdd = (String) addList.elementAt(i);
			vars = getVars(anAdd, vars);
		}
		// deleteListの変数を集める
		for (int i = 0; i < deleteList.size(); i++) {
			String aDelete = (String) deleteList.elementAt(i);
			vars = getVars(aDelete, vars);
		}
		Hashtable renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

		// 新しいIfListを作る
		Vector newIfList = new Vector();
		for (int i = 0; i < ifList.size(); i++) {
			String newAnIf = renameVars((String) ifList.elementAt(i),
					renamedVarsTable);
			newIfList.addElement(newAnIf);
		}
		// 新しいaddListを作る
		Vector newAddList = new Vector();
		for (int i = 0; i < addList.size(); i++) {
			String newAnAdd = renameVars((String) addList.elementAt(i),
					renamedVarsTable);
			newAddList.addElement(newAnAdd);
		}
		// 新しいdeleteListを作る
		Vector newDeleteList = new Vector();
		for (int i = 0; i < deleteList.size(); i++) {
			String newADelete = renameVars((String) deleteList.elementAt(i),
					renamedVarsTable);
			newDeleteList.addElement(newADelete);
		}
		// 新しいnameを作る
		String newName = renameVars(name, renamedVarsTable);

		return new Operator(newName, newIfList, newAddList, newDeleteList, isRelatedNextOperator);
	}

	private Vector getVars(String thePattern, Vector vars) {
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				vars.addElement(tmp);
			}
		}
		return vars;
	}

	private Hashtable makeRenamedVarsTable(Vector vars, int uniqueNum) {
		Hashtable result = new Hashtable();
		for (int i = 0; i < vars.size(); i++) {
			String newVar = (String) vars.elementAt(i) + uniqueNum;
			result.put((String) vars.elementAt(i), newVar);
		}
		return result;
	}

	private String renameVars(String thePattern, Hashtable renamedVarsTable) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " + (String) renamedVarsTable.get(tmp);
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	public Operator instantiate(Hashtable theBinding) {
		// name を具体化
		String newName = instantiateString(name, theBinding);
		// ifList を具体化
		Vector newIfList = new Vector();
		for (int i = 0; i < ifList.size(); i++) {
			String newIf = instantiateString((String) ifList.elementAt(i),
					theBinding);
			newIfList.addElement(newIf);
		}
		// addList を具体化
		Vector newAddList = new Vector();
		for (int i = 0; i < addList.size(); i++) {
			String newAdd = instantiateString((String) addList.elementAt(i),
					theBinding);
			newAddList.addElement(newAdd);
		}
		// deleteListを具体化
		Vector newDeleteList = new Vector();
		for (int i = 0; i < deleteList.size(); i++) {
			String newDelete = instantiateString(
					(String) deleteList.elementAt(i), theBinding);
			newDeleteList.addElement(newDelete);
		}
		return new Operator(newName, newIfList, newAddList, newDeleteList, isRelatedNextOperator);
	}

	private String instantiateString(String thePattern, Hashtable theBinding) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				String newString = (String) theBinding.get(tmp);
				if (newString == null) {
					result = result + " " + tmp;
				} else {
					result = result + " " + newString;
				}
			} else {
				result = result + " " + tmp;
			}
		}
		return result.trim();
	}

	private boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}
}

class Unifier {
	StringTokenizer st1;
	String buffer1[];
	StringTokenizer st2;
	String buffer2[];
	Hashtable vars;

	Unifier() {
		// vars = new Hashtable();
	}

	public boolean unify(String string1, String string2, Hashtable theBindings, boolean isRelatedNextOperator) {
		Hashtable orgBindings = new Hashtable();
		for (Enumeration e = theBindings.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = (String) theBindings.get(key);
			orgBindings.put(key, value);
		}
		this.vars = theBindings;
		if (unifyToken(string1, string2, theBindings, isRelatedNextOperator)) {
			return true;
		} else {
			// 失敗したら元に戻す．
			theBindings.clear();
			for (Enumeration e = orgBindings.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String value = (String) orgBindings.get(key);
				theBindings.put(key, value);
			}
			return false;
		}
	}

	// 関数名変更した
	public boolean unifyToken(String string1, String string2, Hashtable theBindings, boolean isRelatedNextOperator) {
		// 同じなら成功
		if (string1.equals(string2))
			return true;

		// 各々トークンに分ける
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 数が異なったら失敗
		if (st1.countTokens() != st2.countTokens())
			return false;

		// 定数同士
		int length = st1.countTokens();
		buffer1 = new String[length];
		buffer2 = new String[length];
		for (int i = 0; i < length; i++) {
			buffer1[i] = st1.nextToken();
			buffer2[i] = st2.nextToken();
		}

		// 初期値としてバインディングが与えられていたら
		if (this.vars.size() != 0) {
			for (Enumeration keys = vars.keys(); keys.hasMoreElements();) {
				String key = (String) keys.nextElement();
				String value = (String) vars.get(key);
				replaceBuffer(key, value);
			}
		}

		Integer varTokenCount[] = {0};
		Integer matchPreviousResultCount[] = {0};

		for (int i = 0; i < length; i++) {
			if (!tokenMatching(buffer1[i], buffer2[i], theBindings, varTokenCount, matchPreviousResultCount)) {
				return false;
			}
		}

		if(isRelatedNextOperator && varTokenCount == matchPreviousResultCount){
			return false;
		}

		return true;
	}

	// 課題7-1 変更箇所
	boolean tokenMatching(String token1, String token2, Hashtable theBinding, Integer[] varTokenCount, Integer[] matchPreviousResultCount) {
		if (token1.equals(token2)){
			return true;
		}
		if (var(token1) && !var(token2)){
			++varTokenCount[0];
			return varMatching(token1, token2, theBinding, matchPreviousResultCount);
		}
		if (!var(token1) && var(token2)){
			++varTokenCount[0];
			return varMatching(token2, token1, theBinding, matchPreviousResultCount);
		}
		if (var(token1) && var(token2)){
			return varMatching(token1, token2);
		}
		return false;
	}

	boolean varMatching(String vartoken, String token) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			replaceBuffer(vartoken, token);
			if (vars.contains(vartoken)) {
				replaceBindings(vartoken, token);
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	boolean varMatching(String vartoken, String token, Hashtable theBinding, Integer[] matchPreviousResultCount) {
		if (vars.containsKey(vartoken)) {
			if (token.equals(vars.get(vartoken))) {
				return true;
			} else {
				return false;
			}
		} else {
			String uniqueNum = vartoken.replaceAll("[^0-9]", "");
			for (Enumeration e = theBinding.keys(); e.hasMoreElements();) {
				// 同じアサーション内の異なる変数で同じ値が割り当てられた際の処理
				String key = (String) e.nextElement();
				String value = (String) theBinding.get(key);
				if(key.contains(uniqueNum) && value.equals(token)){
					return false;
				}
				else if(key.contains(String.valueOf(Integer.parseInt(uniqueNum) - 1)) && value.equals(token)){
					++matchPreviousResultCount[0];
				}
			}
			replaceBuffer(vartoken, token);
			if (vars.contains(vartoken)) {
				replaceBindings(vartoken, token);
			}
			vars.put(vartoken, token);
		}
		return true;
	}

	void replaceBuffer(String preString, String postString) {
		for (int i = 0; i < buffer1.length; i++) {
			if (preString.equals(buffer1[i])) {
				buffer1[i] = postString;
			}
			if (preString.equals(buffer2[i])) {
				buffer2[i] = postString;
			}
		}
	}

	void replaceBindings(String preString, String postString) {
		Enumeration keys;
		for (keys = vars.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			if (preString.equals(vars.get(key))) {
				vars.put(key, postString);
			}
		}
	}

	boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

}
