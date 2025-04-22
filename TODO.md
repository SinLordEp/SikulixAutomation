
## General
1. ~~Exception handler needs to be done~~
2. ~~Divide controller into smaller services~~
3. ~~Extract JOptionPanel methods to a Utils class~~
4. ~~Make the GUI more beautiful~~
5. ~~Use implemented GraalVM to build an executable(Canceled, not working well with swing)~~
6. ~~Build a single jar, then pack it with JPackage to a single executable~~
7. ~~Modify all file paths based on project or jar parent path to make it more consistent~~
8. ~~Generate the test result with specific failed step and info~~
9. ~~Implement logger~~
10. ~~Add support for input JSON, then repeat the same testStep while data remain in CaseExecuteService~~
11. ~~Add param for StepExecuteService when JSON is needed and inject the JsonObject~~
12. Add a control for eliminating the last space char in a path if it exists



## ToolGUI
1. ~~ToolGUI lost focus after any operation, should at least stay on the first one or the chosen one~~
2. ~~Result table data is not center alignment~~
3. ~~Add a way to edit a step, such as double-click it~~
4. ~~It should have control of data isChanged to warn the user before closing or loading another config~~
5. ~~Target window for testing should be set on top / always on top~~
6. ~~Add a specific step column in the result table~~
7. ~~Add control to prevent multi click on Start test button~~
8. ~~Add a method to modify TestCase name and its image path along~~
9. ~~Add a method to disable the delete step when no step is selected(done by not working if the index is -1)~~
10. ~~Add a text field or a button for getting the window name~~
11. ~~Add a progress bar to visualize test plan progress~~
12. Add a loop testing option
13. Add a repeat time input field for testing
14. ~~Add a select all button for TestCase~~
15. ~~Extract buildTestPlan method to service.~~
16. ~~Add a Build test plan button instead of using the Start button to build and run at the same time~~
17. Add tooltip to disable button
18. Add control to empty name input and cancel/window closing on rename/create
19. Make testStepGUI pop up location related to ToolGUI
20. Make getPath pop up location related to ToolGUI and its default name should change by operation
21. ~~Add function to reorder JList~~
22. ~~Add a panel to import json~~
23. ~~Build plan will check if JSON is selected and contains all the params for testcase (event)~~

## TestStepGUI
1. ~~TestStep region toggle will not change by changing stats in the step info panel~~
2. ~~Image preview border~~
3. ~~Compiled TestStep~~
4. ~~Element toggle button will move after the panel is visible~~
5. ~~Extract the huge ass element panel creation method to smaller methods~~
6. ~~Image name or text label should add a listener linked to radiobutton(Canceled)~~
7. ~~Add a timeoutSec text field~~
8. ~~Add a similarity text field~~
9. Add a read image from a file option and copy it to the target root
10. ~~Add enter key boolean checkbox~~
11. ~~Add control to image name and step name~~
12. ~~Mark the none-null element as green~~
13. ~~Auto generate image name by its testCase, step and element type name~~


## TestCaseGUI
1. Modify the name field
2. JList with step needed params


