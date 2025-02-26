package com.gsc.kixxhub.module.pumpm.pump.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Test {

/**
 *  리스트 2 디스팅트_리스트
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
			String CENTER = ""; // 센터코드
			String CENTER2 = ""; // 중복카운트 위함.
			int TOTAL_WORK = 1;
			int maxSeq = this.getCenterIssueMaxNum() + 1; // 센터현황 최대시퀀스값
			for (int i = 0; i < objListSize; i++) { // 배열은 0부터 시작. 리스트 사이즈 만큼 돌린다.
				objMap = (HashMap) objList.get(i); // 리스트에서 뽑아낸 오브젝트를 HashMap으로 캐스트.
				issMap = new HashMap();

				CENTER = (String) objMap.get("CENTER");
				if (CENTER.equals(CENTER2)) { // 현재 가져온 센터코드와 바로전 센터코드를 비교하여 중복이면 TRUE
					TOTAL_WORK = TOTAL_WORK + 1; // 대상국소수를 증가한다.
					issList.remove(issList.size() - 1); // 바로 전 해시맵 오브젝트를 지운다.
				} else { // 현재 가져온 센터코드와 바로전 센터코드를 비교하여 중복이면 TRUE
					TOTAL_WORK = 1; // 국소수를 리셋한다.
				}
				issMap.put("CENTER", objMap.get("CENTER")); // 현재의 해시맵 오브젝트 추가한다.(센터코드))
				issMap.put("TOTAL_WORK", TOTAL_WORK + ""); // 현재의 해시맵 오브젝트를 추가한다.(대상국소수)
				issMap.put("ISSUE_SEQ", maxSeq + issList.size() + ""); // 시퀀스 뽑아내기
				issList.add(issMap); // 위에서 만든 해시맵 오브젝트를 리스트에 추가한다.

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
