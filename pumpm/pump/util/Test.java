package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Test {

/**
 *  ����Ʈ 2 ����Ʈ_����Ʈ
 *
 * @param List
 * @return List
 * @throws  Exception 
 */ 
	public List getCenterDistinctList(List objList) throws Exception {
 
		List issList = new ArrayList();
		try {

			HashMap objMap = new HashMap();
			HashMap issMap = new HashMap();
			int objListSize = objList.size();
			System.out.println("###############");
			System.out.println("##objListSize : "+ objListSize);
			System.out.println("###############");
			String CENTER = ""; // �����ڵ�
			String CENTER2 = ""; // �ߺ�ī��Ʈ ����.
			int TOTAL_WORK = 1;
			int maxSeq = this.getCenterIssueMaxNum() + 1; // ������Ȳ �ִ��������
			for (int i = 0; i < objListSize; i++) { // �迭�� 0���� ����. ����Ʈ ������ ��ŭ ������.
				objMap = (HashMap) objList.get(i); // ����Ʈ���� �̾Ƴ� ������Ʈ�� HashMap���� ĳ��Ʈ.
				issMap = new HashMap();

				CENTER = (String) objMap.get("CENTER");
				if (CENTER.equals(CENTER2)) { // ���� ������ �����ڵ�� �ٷ��� �����ڵ带 ���Ͽ� �ߺ��̸� TRUE
					TOTAL_WORK = TOTAL_WORK + 1; // ��󱹼Ҽ��� �����Ѵ�.
					issList.remove(issList.size() - 1); // �ٷ� �� �ؽø� ������Ʈ�� �����.
				} else { // ���� ������ �����ڵ�� �ٷ��� �����ڵ带 ���Ͽ� �ߺ��̸� TRUE
					TOTAL_WORK = 1; // ���Ҽ��� �����Ѵ�.
				}
				issMap.put("CENTER", objMap.get("CENTER")); // ������ �ؽø� ������Ʈ �߰��Ѵ�.(�����ڵ�))
				issMap.put("TOTAL_WORK", TOTAL_WORK + ""); // ������ �ؽø� ������Ʈ�� �߰��Ѵ�.(��󱹼Ҽ�)
				issMap.put("ISSUE_SEQ", maxSeq + issList.size() + ""); // ������ �̾Ƴ���
				issList.add(issMap); // ������ ���� �ؽø� ������Ʈ�� ����Ʈ�� �߰��Ѵ�.

				CENTER2 = (String) objMap.get("CENTER");

			}
			System.out.println("###############");
			System.out.println("#getCenterDistinctList#");
			System.out.println(issList);
			System.out.println("###############");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return issList;
	}

	public int getCenterIssueMaxNum() {
		// TODO Auto-generated method stub
		return 100;
	}
	
	
	public static void main(String args[]) throws Exception{
		List tempList = new ArrayList<>();
		String[][] tempArray = new String [3][3];
		byte alphabet = 65;
		for(int i=0; i<tempArray.length; i++){
			for(int j=0; j<tempArray[i].length; j++){
				alphabet = (byte) (alphabet + i);
				tempArray[i][0] = "ID";
				tempArray[i][1] = "NAME";
				tempArray[i][2] =  (char) alphabet+"";
			}
		}
		
		tempList.add(tempArray);
		Test test = new Test();
		test.getCenterDistinctList(tempList);
	}
}
