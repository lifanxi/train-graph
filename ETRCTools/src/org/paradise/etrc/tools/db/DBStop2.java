package org.paradise.etrc.tools.db;

/*
CREATE TABLE STOP2
(
  SINGLE_NAME  VARCHAR2(5 BYTE)                 NOT NULL,
  STATION_ID   NUMBER(4)                        NOT NULL,
  ARRIVE_TIME  VARCHAR2(5 BYTE),
  LEAVE_TIME   VARCHAR2(5 BYTE),
  DISTANCE     NUMBER(4)
)
 */

public class DBStop2 {
    public String singleName;
    public String stationID;
    public String arriveTime;
    public String leaveTime;
    public int distance;
}
