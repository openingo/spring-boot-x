/*
 * MIT License
 *
 * Copyright (c) 2021 OpeningO Co.,Ltd.
 *
 *    https://openingo.org
 *    contactus(at)openingo.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.openingo.spring.validator;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

/**
 * dynamic validator
 *
 * @author Qicz
 */
public interface DynamicValidator {

    /**
     * dynamic validate
     */
    void dynamicValidate();

    /**
     * dynamic validate at silently
     * @return error message
     */
    default String silentDynamicValidate() {
        String errorMessage = null;
        try {
            this.dynamicValidate();
        } catch (ConstraintViolationException ex) {
            Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
            errorMessage = ex.getMessage();
            if (null != constraintViolations && constraintViolations.size() > 0) {
                Optional<ConstraintViolation<?>> first = constraintViolations.stream().findFirst();
                errorMessage = first.map(ConstraintViolation::getMessage).get();
            }
        }
        return errorMessage;
    }

    /**
     * validate accessor
     * @param inValid true invalid，false valid
     * @param defaultMessage tips message
     */
    default void validateField(boolean inValid, String defaultMessage) {
        if (inValid) {
            this.throwValidationException(defaultMessage);
        }
    }

    /**
     * validate self
     * @param data self
     * @param groups validate groups
     * @param <T>
     */
    default <T> void validateSelf(T data, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> validate = validator.validate(data, groups);
        this.parserValidateRet(validate);
    }

    /**
     * validate the data is invalid or not
     * @param data that is validating data
     * @param errorMessage default error message
     * @param groups validate groups
     * @param <T>
     */
    default <T extends DynamicValidator> void dynamicValidate(T data, String errorMessage, Class<?>... groups) {
        if (null == data) {
            this.throwValidationException(errorMessage);
            return;
        }
        this.validateSelf(data, groups);
        data.dynamicValidate();
    }

    /**
     * throw one validation 'ConstraintViolationException' exception
     * @param message exception message
     */
    default void throwValidationException(String message){
        throw new ConstraintViolationException(message, null);
    }

    /**
     * parser the validate result
     * @param validate ConstraintViolation set
     */
    default <T> void parserValidateRet(Set<ConstraintViolation<T>> validate) {
        if (null != validate && validate.size() > 0) {
            throw new ConstraintViolationException(validate);
        }
    }
}
