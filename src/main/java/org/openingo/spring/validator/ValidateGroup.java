package org.openingo.spring.validator;

/**
 * ValidateGroup
 *
 * @author Qicz
 */
public interface ValidateGroup {

    /**
     * create logic group
     */
    interface Create {}

    /**
     * read logic group
     */
    interface Read {}

    /**
     * update logic group
     */
    interface Update {}

    /**
     * delete logic group
     */
    interface Delete {}
}
