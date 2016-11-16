# ubtrobot-alpha2-demo
Android project for Alpha2 to make it do fun stuff for Uppstart in Uppsala 2016

## Alpha 2 from ubrobotics

This app say a text (Welcome to Uppstart, hope you have fun!) and then play a preset song with its moves. Repeat this until the button is pressed again.

### A few notes:

* Download the API (http://dev.ubtrobot.com/opencenter/index) and copy the ubtechalpha2robot.jar file into alpha2demo/libs directory. Did not include it since you would need to agree to the license.
* Trying to develop using the two phones did not work for me. Could never install the emulator APK, wrong signature on the APK file.
* When deploying the app, aka pressing play in Android studio, please note that I always needed to disconnect the app and then re-connect to the Alpha2 before the changes took effect.
* The code has been cleaned up a bit from the demo version included in the SDK from ubtrobot.

## TO DO

1. Change to let the user enter the text it should say.
2. Have a selection list of pre-installed moves it can do.
3. Add a new App ID and publish the app on the Alpha 2 store.
	Did not work, got "Not found" error message from attempting to access the "http://dev.ubtrobot.com/opencenter/app/complete" page.
4. Add explanations to the code to show how the different callbacks/interfaces works.
