package org.paradise.etrc.tools.db;

/***************************************************************************************

CREATE TABLE TRAIN
(
  TRAIN_ID     NUMBER(4),
  TRAIN_NAME   VARCHAR2(25 BYTE)                NOT NULL,
  CAR_TYPE     NUMBER(1),
  TRAIN_LEVEL  NUMBER(1)
)

COMMENT ON COLUMN TRAIN.TRAIN_ID IS '车次ID';

COMMENT ON COLUMN TRAIN.TRAIN_NAME IS '车次全称';

COMMENT ON COLUMN TRAIN.CAR_TYPE IS '车体类型－－0：普车，1：新空';

COMMENT ON COLUMN TRAIN.TRAIN_LEVEL IS '列车等级－－0：普客，1：普快，2：快速，3：特快，4：直特';

***************************************************************************************/

public class DBTrain {
	public static final int TYPE_PC = 0;
	public static final int TYPE_XK = 1;
	
	public static final int LEVEL_PKe = 0;
	public static final int LEVEL_PKa = 1;
	public static final int LEVEL_KS = 2;
	public static final int LEVEL_TK = 3;
	public static final int LEVEL_ZT = 4;

	public int type;
	public int level;
	public String trainName;
}
