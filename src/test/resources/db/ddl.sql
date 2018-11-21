CREATE TABLE `DATA_BEAN` (
  `GUID` varchar(60) NOT NULL,
  `UNIQUE_ID` varchar(60) NOT NULL,
  `EVENT_ID` varchar(15) DEFAULT NULL,
  `VALUE_DATE` timestamp NULL DEFAULT NULL,
  `VERSION` int(11) DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL,
  `CREATED_BY` varchar(10) NOT NULL,
  `UPDATED_AT` timestamp NULL DEFAULT NULL,
  `UPDATED_BY` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`UNIQUE_ID`)
) ENGINE=InnoDB ;
