== Troubleshooting ==
For some stupid reason you the default permissions of the mysql jar file are not sufficient. You have to ssh into the server and chmod 777 *jar

== Deploying ===

1. Run GWT compile in Eclipse
(I haven't got it working with maven)

=== Copy paste ===

==== 2. SSH-mount AWS EC2 instance====
 cd ~/Desktop/ && \
 sshfs -o IdentityFile=/Users/sarnobat/.ssh/sarnobat.pem ec2-user@ec2-54-234-94-4.compute-1.amazonaws.com:/ aws 

==== 3. Create the war and scp it to the server ===

 cd /Users/sarnobat/Desktop/mac-sync/src/java/ImageBlog/war/ && \
 touch ImageBlog.war && \
 rm ImageBlog.war && \
 touch WEB-INF/lib/gwt-servlet.jar WEB-INF/lib/appengine- WEB-INF/lib/aws && \
 rm WEB-INF/lib/gwt-servlet.jar WEB-INF/lib/appengine-* WEB-INF/lib/aws* && \
 zip -r ImageBlog.war * && \
 scp -i ~/.ssh/sarnobat.pem /Users/sarnobat/Desktop/mac-sync/src/java/ImageBlog/war/ImageBlog.war ec2-user@ec2-54-234-94-4.compute-1.amazonaws.com:/usr/share/tomcat7/ && \
 touch /Users/sarnobat/Desktop/aws/usr/share/tomcat7/ImageBlog.war && \
 mv /Users/sarnobat/Desktop/aws/usr/share/tomcat7/ImageBlog.war /Users/sarnobat/Desktop/aws/var/lib/tomcat7/webapps/ROOT.war && \
 rm /Users/sarnobat/Desktop/mac-sync/src/java/ImageBlog/war/ImageBlog.war 
 
 

 ssh -i ~/.ssh/sarnobat.pem ec2-user@ec2-54-234-94-4.compute-1.amazonaws.com
 
 # note for port 80 you must sudo
 sudo /home/ec2-user/apache-tomcat-7.0.34/bin/shutdown.sh && sudo /home/ec2-user/apache-tomcat-7.0.34/bin/startup.sh && tail -f apache-tomcat-7.0.34/logs/catalina.out
 #/home/ec2-user/apache-tomcat-7.0.34/bin/startup.sh
 #sudo chmod 777 /var/lib/tomcat7/webapps/ROOT/WEB-INF/lib/* -R
# sudo chown ec2-user /var/lib/tomcat7/webapps/ROOT/WEB-INF/lib/* -R
# sudo ./shutdown.sh && sudo ./startup.sh && tail -f ../logs/catalina.out ../logs/localhost.2013-01-13.log 
# sudo service tomcat7 stop && sudo service tomcat7 start && tail -f /var/log/tomcat7/catalina.out

 tail -f /var/log/tomcat7/catalina.out
 tail -f /Users/sarnobat/Desktop/aws/var/log/tomcat7/catalina.out

== Logging ==
 tail -f /Users/sarnobat/Desktop/aws/var/log/tomcat7/catalina.out 
 
== Directories ==
Dev:
/Users/sarnobat/Desktop/mac-sync/src/java/ImageBlog/war/

Symbolic links:
/Users/sarnobat/Desktop/aws/usr/share/tomcat7

Log
/Users/sarnobat/Desktop/aws/var/log/tomcat7/catalina.out

Webapps
/Users/sarnobat/Desktop/aws/var/lib/tomcat7/webapps

Conf
/Users/sarnobat/Desktop/aws/etc/tomcat7/logging.properties

sudo service tomcat7 stop && sudo service tomcat7 start && tail -f /var/log/tomcat7/catalina.out  

== ssh ==
 ssh -i ~/.ssh/sarnobat.pem ec2-user@ec2-54-234-94-4.compute-1.amazonaws.com