FROM tomcat:9-jdk11
VOLUME /config

ADD /target/psimi-validator-view.war "/usr/local/tomcat/webapps/intact#validator.war"
RUN cp -r webapps.dist/ROOT webapps/
RUN cp -r webapps.dist/manager webapps/

ADD https://raw.githubusercontent.com/HUPO-PSI/psi-mi-CV/master/psi-mi.obo /config/
ADD https://raw.githubusercontent.com/HUPO-PSI/psi-mod-CV/master/PSI-MOD.obo /config/
ADD https://raw.githubusercontent.com/MICommunity/psidev/master/psi/mi/controlledVocab/proteomeBinder/psi-par.obo /config/

CMD ["catalina.sh", "run"]