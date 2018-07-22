## To get bitnami parse server credentials: https://docs.bitnami.com/aws/faq/starting-bitnami-aws/find_credentials/ 
## Make sure the EC2 instance is launched when running the app. Then you have to be connected to it. 
### If the instance was stopped and then restarted:
 - you might have to reconnect to the instance via the [aws console](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html?icmpid=docs_ec2_console) . 
 - Once connected and the terminal shows up, go to apps/parse/htdocs and oepn server.js . Include the relevant information in StarterApplication.java , and then open up the server url to see the data. 
 - Don't forget to the slash at the end of the url