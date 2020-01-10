package renderer;

import com.sun.scenario.effect.ImageData;

import java.util.ArrayList;

/**
 * 串行地渲染页面元素
 *
 * @author wulang
 * @create 2020/1/10/14:21
 */
public class SingleThreadRenderer {
    void rederPage(CharSequence souce){
        renderText(souce);
        ArrayList<ImageData> imageData = new ArrayList<>();
        for (ImageInfo imageInfo : scanForImage(souce)){
            imageData.add(imageInfo.downloadImage());
        }
        for (ImageData data : imageData){
            renderImage(data);
        }
    }

    private void renderImage(ImageData data) {

    }

    private ImageInfo[] scanForImage(CharSequence souce) {
        return new ImageInfo[0];
    }

    private void renderText(CharSequence souce) {

    }
}

class ImageInfo{

    public ImageData downloadImage() {
        return null;
    }
}
