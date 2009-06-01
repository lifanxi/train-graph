package org.paradise.etrc.wizard.addtrain;

import java.util.Vector;

import org.paradise.etrc.data.Train;
import org.paradise.etrc.dialog.MessageBox;
import org.paradise.etrc.dialog.YesNoBox;
import org.paradise.etrc.view.chart.ChartView;
import org.paradise.etrc.wizard.Wizard;
import org.paradise.etrc.wizard.WizardDialog;

public class AddTrainWizard extends Wizard {
	private Train train;
	private ChartView chartView;
	
	public AddTrainWizard(ChartView _view) {
		wizardSteps = 4;
		chartView = _view;
	}
	
	public int doWizard() {
		WZTrainNameInput step1 = new WZTrainNameInput(chartView.mainFrame, 1, "添加新车", "输入车次");
		WZTimeEdit step2 = new WZTimeEdit(chartView.mainFrame, 2, "添加新车", "编辑点单");
		WZInPointSet step3 = new WZInPointSet(chartView.mainFrame, 3, "添加新车", "确定入图（始发）点");
		WZOutPointSet step4 = new WZOutPointSet(chartView.mainFrame, 4, "添加新车", "确定出图（终到）点");
		
		step2.canPrev = false;
//		step3.canNext = false;
//		step1.canFinish = false;
		step2.canFinish = false;

		String fullName;
		String downName = null;
		String upName = null;
		
		train = null;
		
		int curStep = 1;
		int rt = WizardDialog.NEXT;
		while(rt != WizardDialog.CANCEL && rt != WizardDialog.FINISH) {
			switch(curStep) {
			case 1:
				rt = step1.doModal();
				break;
			case 2:
				if(train == null) {
					fullName = step1.getFullName();
					downName = step1.getDownName();
					upName = step1.getUpName();
					
					
					if(new YesNoBox(chartView.mainFrame, "是否查询内置时刻表获取点单数据？").askForYes()) {
						String[] names = fullName.split("/");
						Vector trains = new Vector();
						for(int i=0; i<names.length; i++) {
							Vector fTras = chartView.mainFrame.getSKB().getTrains(names[i]);
							for(int j=0; j<fTras.size(); j++) {
								Object obj = fTras.get(j);
								if(!trains.contains(obj))
									trains.add(obj);
							}
						}
						
						//找到了几条数据，TODO 如果是多条的话应当选择。
						if(trains.size() == 0) {
							new MessageBox(chartView.mainFrame, "没有找到" + fullName + "次列车的时刻表，请手工输入。").showMessage();
							train = new Train();
							train.trainNameFull = fullName;
							train.trainNameDown = downName;
							train.trainNameUp = upName;
					    }
						else if(trains.size() == 1)
							train = (Train) trains.get(0);
						else {
							train = (Train) trains.get(0);
							String temp = ((Train) trains.get(0)).getTrainName();
							for(int i=1; i<trains.size(); i++) {
								temp = temp + "、" + ((Train) trains.get(i)).getTrainName();
							}
							new MessageBox(chartView.mainFrame, "找到" + trains.size() + "条" + fullName + "次列车的时刻表数据，选用第一条数据("+ train.getTrainName() + ")。（TODO: 请选择" + temp + "）。").showMessage();
						}
					}
					else {
						train = new Train();
						train.trainNameFull = fullName;
						train.trainNameDown = downName;
						train.trainNameUp = upName;
					}

					//看看输入的上下行车次时候与查出来的一致，如果一致则设置为输入的，否则就用时刻表读出来的时候所设的默认的
					Vector myNamesVec = new Vector();
					String[] myNames = train.getTrainName().split("/");
					for(int i=0; i<myNames.length; i++)
						myNamesVec.add(myNames[i]);
					
					if(myNamesVec.contains(downName))
						train.trainNameDown = downName;
					if(myNamesVec.contains(upName))
						train.trainNameUp = upName;
				}

				System.out.println(train);
				step2.setTrain(train, downName, upName);
				System.out.println(train);

				rt = step2.doModal();
				System.out.println(train);
				break;
			case 3:
				step3.setData(chartView.mainFrame.chart, train);
				rt = step3.doModal();
				break;
			case 4:
				step4.setTrain(train);
				rt = step4.doModal();
				break;
			}
			
			if(rt == WizardDialog.NEXT)
				curStep ++;
			else if(rt == WizardDialog.PREV)
				curStep --;
			
			if(curStep < 1)
				curStep = 1;
			else if(curStep > wizardSteps)
				curStep = wizardSteps;
		}
		
		if(rt == WizardDialog.FINISH) {
			return FINISHED;
		}
		else {
			return CANCELED;
		}
	}
	
	public Train getTrain() {
		return train;
	}
}
