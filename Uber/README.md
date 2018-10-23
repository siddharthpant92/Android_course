## Make sure the EC2 instance is launched when running the app. Then you have to be connected to it. 

### To get bitnami parse server credentials: https://docs.bitnami.com/aws/faq/get-started/find-credentials/

### If the instance was stopped and then restarted:
 - You might have to reconnect to the instance via the [aws console](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html?icmpid=docs_ec2_console) . 
 - Once connected and the terminal shows up, go to apps/parse/htdocs and open server.js . Include the relevant information in StarterApplication.java , and then open up the server url (replace parse with apps in the url) to see the data. 
 - Don't forget to add the slash at the end of the url 

### Booking and cancelling scenarios handled: 
 - Rider books, Driver accepts and then cancels => rider should be notified
 - Rider books, Driver accepts => location updates must be visible to rider
 - Rider books, Driver hasn't accepted but changes location => distance between rider and driver should update, driver's location on map should update
 - Rider cancels => Driver should be notified if they have accepted that request. If the driver has selected that request but not yet accepted, driver should still be notified
- If the rider books and then changes location, the location from which they booked is locked. They have to cancel and then re-book


#### Note: Rider cannot search for location manually yet.