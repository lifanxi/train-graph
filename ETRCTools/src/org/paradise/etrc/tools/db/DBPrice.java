package org.paradise.etrc.tools.db;

/*
 * 
CREATE TABLE PRICE ( 
  CAR_TYPE       NUMBER (1)    NOT NULL, 
  TRAIN_LEVEL    NUMBER (1)    NOT NULL, 
  DISTANCE_FROM  NUMBER (4), 
  DISTANCE_TO    NUMBER (4), 
  YINGZUO        NUMBER (4,2), 
  RUANZUO        NUMBER (4,2), 
  YINGWOU        NUMBER (4,2), 
  YINGWOM        NUMBER (4,2), 
  YINGWOD        NUMBER (4,2), 
  RUANWOU        NUMBER (4,2), 
  RUANWOD        NUMBER (4,2)
); 

COMMENT ON COLUMN PRICE.CAR_TYPE IS '车体类型，同Train表'－－0：普车，1：新空'; 
COMMENT ON COLUMN PRICE.TRAIN_LEVEL IS '列车等级，同Train表'－－0：普客，1：普快，2：快速，3：特快，4：直特';
COMMENT ON COLUMN PRICE.DISTANCE_FROM IS '里程区间最小值';
COMMENT ON COLUMN PRICE.DISTANCE_TO IS '里程区间最大值';
COMMENT ON COLUMN PRICE.RUANWOD IS '软卧下铺';
COMMENT ON COLUMN PRICE.RUANWOU IS '软卧上铺';
COMMENT ON COLUMN PRICE.RUANZUO IS '软座票价';
COMMENT ON COLUMN PRICE.YINGWOD IS '硬卧下铺';
COMMENT ON COLUMN PRICE.YINGWOM IS '硬卧中铺';
COMMENT ON COLUMN PRICE.YINGWOU IS '硬卧上铺';
COMMENT ON COLUMN PRICE.YINGZUO IS '硬座票价';
 *
 */

public class DBPrice {
    public int type;
    public int level;
    public int distFrom;
    public int distTo;
    public int ruanwoD;
    public int ruanwoU;
    public int ruanzuo;
    public int yingzuo;
    public int yingwoD;
    public int yingwoM;
    public int yingwoU;

    public DBPrice copy() {
        DBPrice price = new DBPrice();

        price.type = this.type;
        price.level = this.level;
        price.distFrom = this.distFrom;
        price.distTo = this.distTo;
        price.ruanwoD = this.ruanwoD;
        price.ruanwoU = this.ruanwoU;
        price.ruanzuo = this.ruanzuo;
        price.yingzuo = this.yingzuo;
        price.yingwoD = this.yingwoD;
        price.yingwoM = this.yingwoM;
        price.yingwoU = this.yingwoU;

        return price;
    }

    public String toString() {
        return "[" + type + level + "]"
                + "(" + distFrom + "-" + distTo + ")"
                + "YZ:" + yingzuo
                + "|RZ:" + ruanzuo
                + "|YWU:" + yingwoU
                + "|YWM:" + yingwoM
                + "|YWD:" + yingwoD
                + "|RWU:" + ruanwoU
                + "|RWD:" + ruanwoD;
    }
}
