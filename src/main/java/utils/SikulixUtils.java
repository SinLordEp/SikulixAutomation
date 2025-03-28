package utils;

import exceptions.UndefinedException;
import model.TestStep;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class SikulixUtils {
    public static final Screen screen = new Screen();
    private static final String imageRoot = "testcaseimage/";
    static{
        Settings.OcrTextRead = true;
        Settings.OcrTextSearch = true;
    }

    @FunctionalInterface
    interface FindFailedReturnBool {
        void run() throws FindFailed;
    }

    private SikulixUtils(){}

    public static TestState handleTestStep(TestStep step){
        TestState state = switch (step.getAction()){
            case FIND -> findImage(step);
            case CLICK -> clickOnFound(step);
            case TYPE, PASTE -> clickAndText(step);
        };
        // retry when retry image is not null and has match by clicking
        if(state == TestState.NO_MATCH && step.getRetryImagePath() != null && handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getRetryImagePath()).similar(step.getSimilarity()), step.getTimeoutSec()).click()) == TestState.MATCHED){
                state = findImage(step);
        }
        //return NO_MATCH when image has no match and error image is null
        if(state == TestState.NO_MATCH && step.getErrorImagePath() == null){
            return TestState.NO_MATCH;
        }
        //return FAIL when image has no match and error image is matched
        if(handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getErrorImagePath()).similar(step.getSimilarity()), step.getTimeoutSec())) == TestState.MATCHED){
            return TestState.FAIL;
        }
        return TestState.PASS;
    }

    private static TestState findImage(TestStep step){
        return handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getImagePath()).similar(step.getSimilarity()), step.getTimeoutSec()));
    }

    public static boolean compareImagesByPixel(BufferedImage firstImage, BufferedImage secondImage){
        if (firstImage.getWidth() != secondImage.getWidth() || firstImage.getHeight() != secondImage.getHeight()) return false;
        for (int y = 0; y < firstImage.getHeight(); y++) {
            for (int x = 0; x < firstImage.getWidth(); x++) {
                if (firstImage.getRGB(x, y) != secondImage.getRGB(x, y)) return false;
            }
        }
        return true;
    }

    private static TestState clickOnFound(TestStep step){
        return handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getImagePath()).similar(step.getSimilarity()), step.getTimeoutSec()).click());
    }

    private static TestState clickOnText(TestStep step){
        return handleFindFailed(() -> step.getRegion().waitText(step.getOutputText(), step.getTimeoutSec()).click());
    }

    private static TestState clickAndText(TestStep step){
        return handleFindFailed(() ->{
            clickOnFound(step);
            if (Objects.requireNonNull(step.getAction()) == TestAction.TYPE) {
                step.getRegion().type(step.getOutputText());
            } else if (step.getAction() == TestAction.PASTE) {
                step.getRegion().paste(step.getOutputText());
            }
            if(step.isEnterKey()){
                step.getRegion().type(Key.ENTER);
            }
        });
    }

    public static void setImagePath(String folderName){
        ImagePath.reset();
        ImagePath.add(imageRoot + "universal");
        ImagePath.add(imageRoot + folderName + "/");
    }

    public static String getImagePath(){
        return ImagePath.getBundlePath();
    }

    public static void addImagePath(String path){
        ImagePath.add(path);
    }

    private static TestState handleFindFailed(FindFailedReturnBool function){
        try{
            function.run();
            return TestState.MATCHED;
        }catch (FindFailed e){
            return TestState.NO_MATCH;
        }catch(Exception e){
            throw new UndefinedException("Undefined exception was threw in Sikulix matching process");
        }
    }

}
