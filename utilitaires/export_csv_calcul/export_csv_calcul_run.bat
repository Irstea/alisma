%~d0
cd %~dp0
java -Xms512M -Xmx2048M -cp .;../lib/routines.jar;../lib/commons-collections-3.2.jar;../lib/ini4j-0.5.1.jar;../lib/log4j-1.2.15.jar;../lib/log4j-1.2.16.jar;../lib/dom4j-1.6.1.jar;../lib/talendcsv.jar;../lib/trove.jar;../lib/jakarta-oro-2.0.8.jar;../lib/xpathutil-1.0.0.jar;../lib/jaxen-1.1.1.jar;../lib/advancedPersistentLookupLib-1.0.jar;../lib/jboss-serialization.jar;export_csv_calcul_0_1.jar; alisma.export_csv_calcul_0_1.export_csv_calcul --context=Default %* 