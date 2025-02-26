#!/bin/bash
JAVA_HOME=/opt/xpd_runtime_final/J9SE5_612
CLASSPATH=./:$JAVA_HOME/jre/lib:/opt/xpd_runtime_final/org.apache.log4j_1.2.13.v200706111418.jar:/root/kixxhub/adaptor/pump
PROPERTIESPATH=/root/kixxhub/adaptor/pump
#PROPERTIESPATH=/opt/xpd_runtime_final
$JAVA_HOME/jre/bin/java -classpath $CLASSPATH -Dload.properties.from=$PROPERTIESPATH com.gsc.kixxhub.adaptor.pump.AdaptorWrapperImp_SomoSelfN2
