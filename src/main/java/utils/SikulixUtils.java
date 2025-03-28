package utils;

import exceptions.UndefinedException;
import model.TestStep;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class SikulixUtils {
    public static final Screen screen = new Screen();
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
        return switch (step.getAction()){
            case FIND -> findImage(step);
            case CLICK -> clickOnFound(step);
            case TYPE, PASTE -> clickAndText(step);
        };
    }

    private static TestState findImage(TestStep step){
        TestState state = handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getImagePath()).similar(step.getSimilarity()), step.getTimeoutSec()));
        if(step.getErrorImagePath() == null && state != TestState.PASS){
            return state;
        }else{
            if(handleFindFailed(() -> step.getRegion().wait(new Pattern(step.getErrorImagePath()).similar(step.getSimilarity()), step.getTimeoutSec())) == TestState.PASS){
                state = TestState.FAIL;
            }
        }
        return state;
    }

    public static TestState findImage(Region region, String imagePath, double similarity, int timeoutSec){
        return handleFindFailed(() -> region.wait(new Pattern(imagePath).similar(similarity), timeoutSec));
    }

    public static boolean compareRegionBeforeAfterRunnable(Runnable runnable, Region targetRegion){
        BufferedImage imageBefore = targetRegion.getScreen().capture().getImage();
        runnable.run();
        BufferedImage imageAfter = targetRegion.getScreen().capture().getImage();
        return compareImagesByPixel(imageBefore, imageAfter);
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

    public static boolean compareTwoRegions(Region firstRegion, Region secondRegion){
        return compareImagesByPixel(firstRegion.getScreen().capture().getImage(), secondRegion.getScreen().capture().getImage());
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

    public static String getTextFromRegion(Region region){
        return region.text();
    }

    public static void setImagePath(String path){
        ImagePath.setBundlePath(path);
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
            return TestState.PASS;
        }catch (FindFailed e){
            return TestState.NO_MATCH;
        }catch(Exception e){
            throw new UndefinedException("Undefined exception was threw in Sikulix matching process");
        }
    }

}
