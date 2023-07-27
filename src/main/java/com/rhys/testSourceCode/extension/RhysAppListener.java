package com.rhys.testSourceCode.extension;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 4:34 AM
 */
@Component
public class RhysAppListener implements ApplicationListener<ApplicationEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("RhysAppListener-接受到了应用事件： " + event);
    }
}
