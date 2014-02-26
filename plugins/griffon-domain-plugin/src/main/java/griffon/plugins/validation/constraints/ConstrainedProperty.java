/*
 * Copyright 2008-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.validation.constraints;

import griffon.core.i18n.MessageSource;
import griffon.exceptions.PropertyException;
import griffon.plugins.validation.Errors;
import griffon.plugins.validation.exceptions.ConstraintException;
import griffon.types.Range;
import griffon.util.GriffonClassUtils;
import org.codehaus.griffon.runtime.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

import static griffon.util.CollectionUtils.list;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Provides the ability to set constraints against a properties of a class. Constraints can either be
 * set via the property setters or via the <pre>applyConstraint(String constraintName, Object constrainingValue)</pre>
 * in combination with a constraint constant. Example:
 * <p/>
 * <code>
 * ...
 * <p/>
 * ConstrainedProperty cp = new ConstrainedProperty(owningClass, propertyName, propertyType);
 * if (cp.supportsConstraint(ConstrainedProperty.EmailConstraint.VALIDATION_DSL_NAME)) {
 * cp.applyConstraint(ConstrainedProperty.EmailConstraint.VALIDATION_DSL_NAME, new Boolean(true));
 * }
 * </code>
 * <p/>
 * Alternatively constraints can be applied directly using the java bean getters/setters if a static (as opposed to dynamic)
 * approach to constraint creation is possible:
 * <p/>
 * <code>
 * cp.setEmail(true)
 * </code>
 *
 * @author Graeme Rocher (Grails)
 */
@SuppressWarnings("serial")
public class ConstrainedProperty {
    private static final String ERROR_NAME_BLANK = "Argument 'name' cannot be blank";

    protected static Map<String, List<Object>> constraints = new HashMap<>();
    public static final Map<String, String> DEFAULT_MESSAGES = new HashMap<>();

    static {
        DEFAULT_MESSAGES.put(BlankConstraint.DEFAULT_BLANK_MESSAGE_CODE, BlankConstraint.DEFAULT_BLANK_MESSAGE);
        DEFAULT_MESSAGES.put(MatchesConstraint.DEFAULT_DOESNT_MATCH_MESSAGE_CODE, MatchesConstraint.DEFAULT_DOESNT_MATCH_MESSAGE);
        DEFAULT_MESSAGES.put(CreditCardConstraint.DEFAULT_INVALID_CREDIT_CARD_MESSAGE_CODE, CreditCardConstraint.DEFAULT_INVALID_CREDIT_CARD_MESSAGE);
        DEFAULT_MESSAGES.put(EmailConstraint.DEFAULT_INVALID_EMAIL_MESSAGE_CODE, EmailConstraint.DEFAULT_INVALID_EMAIL_MESSAGE);
        DEFAULT_MESSAGES.put(MaxConstraint.DEFAULT_INVALID_MAX_MESSAGE_CODE, MaxConstraint.DEFAULT_INVALID_MAX_MESSAGE);
        DEFAULT_MESSAGES.put(MaxSizeConstraint.DEFAULT_INVALID_MAX_SIZE_MESSAGE_CODE, MaxSizeConstraint.DEFAULT_INVALID_MAX_SIZE_MESSAGE);
        DEFAULT_MESSAGES.put(MinConstraint.DEFAULT_INVALID_MIN_MESSAGE_CODE, MinConstraint.DEFAULT_INVALID_MIN_MESSAGE);
        DEFAULT_MESSAGES.put(MinSizeConstraint.DEFAULT_INVALID_MIN_SIZE_MESSAGE_CODE, MinSizeConstraint.DEFAULT_INVALID_MIN_SIZE_MESSAGE);
        DEFAULT_MESSAGES.put(RangeConstraint.DEFAULT_INVALID_RANGE_MESSAGE_CODE, RangeConstraint.DEFAULT_INVALID_RANGE_MESSAGE);
        DEFAULT_MESSAGES.put(SizeConstraint.DEFAULT_INVALID_SIZE_MESSAGE_CODE, SizeConstraint.DEFAULT_INVALID_SIZE_MESSAGE);
        DEFAULT_MESSAGES.put(UrlConstraint.DEFAULT_INVALID_URL_MESSAGE_CODE, UrlConstraint.DEFAULT_INVALID_URL_MESSAGE);
        DEFAULT_MESSAGES.put(NotEqualConstraint.DEFAULT_NOT_EQUAL_MESSAGE_CODE, NotEqualConstraint.DEFAULT_NOT_EQUAL_MESSAGE);
        DEFAULT_MESSAGES.put(InListConstraint.DEFAULT_NOT_INLIST_MESSAGE_CODE, InListConstraint.DEFAULT_NOT_IN_LIST_MESSAGE);
        DEFAULT_MESSAGES.put(NullableConstraint.DEFAULT_NULL_MESSAGE_CODE, NullableConstraint.DEFAULT_NULL_MESSAGE);
        DEFAULT_MESSAGES.put(DateConstraint.DEFAULT_INVALID_DATE_MESSAGE_CODE, DateConstraint.DEFAULT_INVALID_DATE_MESSAGE);

        constraints.put(CreditCardConstraint.VALIDATION_DSL_NAME, list().e(CreditCardConstraint.class));
        constraints.put(EmailConstraint.VALIDATION_DSL_NAME, list().e(EmailConstraint.class));
        constraints.put(BlankConstraint.VALIDATION_DSL_NAME, list().e(BlankConstraint.class));
        constraints.put(RangeConstraint.VALIDATION_DSL_NAME, list().e(RangeConstraint.class));
        constraints.put(InListConstraint.VALIDATION_DSL_NAME, list().e(InListConstraint.class));
        constraints.put(UrlConstraint.VALIDATION_DSL_NAME, list().e(UrlConstraint.class));
        constraints.put(SizeConstraint.VALIDATION_DSL_NAME, list().e(SizeConstraint.class));
        constraints.put(MatchesConstraint.VALIDATION_DSL_NAME, list().e(MatchesConstraint.class));
        constraints.put(MinConstraint.VALIDATION_DSL_NAME, list().e(MinConstraint.class));
        constraints.put(MaxConstraint.VALIDATION_DSL_NAME, list().e(MaxConstraint.class));
        constraints.put(MaxSizeConstraint.VALIDATION_DSL_NAME, list().e(MaxSizeConstraint.class));
        constraints.put(MinSizeConstraint.VALIDATION_DSL_NAME, list().e(MinSizeConstraint.class));
        constraints.put(ScaleConstraint.VALIDATION_DSL_NAME, list().e(ScaleConstraint.class));
        constraints.put(NullableConstraint.VALIDATION_DSL_NAME, list().e(NullableConstraint.class));
        constraints.put(NotEqualConstraint.VALIDATION_DSL_NAME, list().e(NotEqualConstraint.class));
        constraints.put(DateConstraint.VALIDATION_DSL_NAME, list().e(DateConstraint.class));
    }

    protected static final Logger LOG = LoggerFactory.getLogger(ConstrainedProperty.class);

    // move these to subclass

    protected String propertyName;
    protected Class<?> propertyType;

    protected Map<String, Constraint> appliedConstraints = new LinkedHashMap<>();
    protected Class<?> owningClass;

    // simple constraints
    private int order = -1;
    private boolean display = true; // whether the property should be displayed
    private boolean enabled = true; // whether the property is enabled
    private boolean editable = true; // whether the property is editable
    private String format; // the format of the property (for example a date pattern)
    private String widget; // the widget to use to render the property
    private boolean password; // whether the property is a password

    private Map<String, Object> attributes = new LinkedHashMap<>(); // a map of attributes of property
    protected MessageSource messageSource;
    private Map<String, Object> metaConstraints = new HashMap<>();

    /**
     * Constructs a new ConstrainedProperty for the given arguments.
     *
     * @param clazz        The owning class
     * @param propertyName The name of the property
     * @param propertyType The property type
     */
    public ConstrainedProperty(@Nonnull Class<?> clazz, @Nonnull String propertyName, @Nonnull Class<?> propertyType) {
        this.owningClass = requireNonNull(clazz, "Argument 'clazz' cannot be null");
        this.propertyName = requireNonBlank(propertyName, "Argument 'propertyName' cannot be blank");
        this.propertyType = requireNonNull(propertyType, "Argument 'propertyType' cannot be null");
    }

    public static void removeConstraint(String name, Class<?> constraintClass) {
        requireNonBlank(name, ERROR_NAME_BLANK);

        List<Object> objects = getOrInitializeConstraint(name);
        objects.remove(constraintClass);
        List<Object> toRemove = new ArrayList<>();
        for (Object object : objects) {
            if (constraintClass.isInstance(object)) {
                toRemove.add(object);
            }
        }
        objects.removeAll(toRemove);
    }

    public static void removeConstraint(String name) {
        requireNonBlank(name, "Argument [name] cannot be null");

        List<Object> objects = getOrInitializeConstraint(name);
        objects.clear();
    }

    public static void registerNewConstraint(String name, Class<?> constraintClass) {
        requireNonBlank(name, "Argument [name] cannot be null");
        if (constraintClass == null || !Constraint.class.isAssignableFrom(constraintClass)) {
            throw new IllegalArgumentException("Argument [constraintClass] with value [" + constraintClass +
                "] is not a valid constraint");
        }

        List<Object> objects = getOrInitializeConstraint(name);
        objects.add(constraintClass);
    }

    private static List<Object> getOrInitializeConstraint(String name) {
        List<Object> objects = constraints.get(name);
        if (objects == null) {
            objects = new ArrayList<>();
            constraints.put(name, objects);
        }
        return objects;
    }

    public static void registerNewConstraint(String name, ConstraintFactory factory) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(factory, "Argument [factory] cannot be null");
        List<Object> objects = getOrInitializeConstraint(name);
        objects.add(factory);
    }

    public static boolean hasRegisteredConstraint(String constraintName) {
        return constraints.containsKey(constraintName) && constraints.get(constraintName).size() > 0;
    }

    /**
     * @return Returns the appliedConstraints.
     */
    public Collection<Constraint> getAppliedConstraints() {
        return appliedConstraints.values();
    }

    /**
     * Obtains an applied constraint by name.
     *
     * @param name The name of the constraint
     * @return The applied constraint
     */
    public Constraint getAppliedConstraint(String name) {
        return appliedConstraints.get(name);
    }

    /**
     * @param constraintName The name of the constraint to check
     * @return Returns true if the specified constraint name is being applied to this property
     */
    public boolean hasAppliedConstraint(String constraintName) {
        return appliedConstraints.containsKey(constraintName);
    }

    /**
     * @return Returns the propertyType.
     */
    public Class<?> getPropertyType() {
        return propertyType;
    }

    /**
     * @return Returns the max.
     */
    @SuppressWarnings({"unchecked", "rawtypes", "ConstantConditions"})
    public Comparable getMax() {
        Comparable maxValue = null;

        MaxConstraint maxConstraint = (MaxConstraint) appliedConstraints.get(MaxConstraint.VALIDATION_DSL_NAME);
        RangeConstraint rangeConstraint = (RangeConstraint) appliedConstraints.get(RangeConstraint.VALIDATION_DSL_NAME);

        if ((maxConstraint != null) || (rangeConstraint != null)) {
            Comparable maxConstraintValue = maxConstraint != null ? maxConstraint.getMaxValue() : null;
            Comparable rangeConstraintHighValue = rangeConstraint != null ? rangeConstraint.getRange().getTo() : null;

            if ((maxConstraintValue != null) && (rangeConstraintHighValue != null)) {
                maxValue = (maxConstraintValue.compareTo(rangeConstraintHighValue) < 0) ? maxConstraintValue : rangeConstraintHighValue;
            } else if ((maxConstraintValue == null) && (rangeConstraintHighValue != null)) {
                maxValue = rangeConstraintHighValue;
            } else if ((maxConstraintValue != null) && (rangeConstraintHighValue == null)) {
                maxValue = maxConstraintValue;
            }
        }

        return maxValue;
    }

    /**
     * @param max The max to set.
     */
    @SuppressWarnings("rawtypes")
    public void setMax(Comparable max) {
        if (max == null) {
            appliedConstraints.remove(MaxConstraint.VALIDATION_DSL_NAME);
            return;
        }

        if (!propertyType.equals(max.getClass())) {
            throw new PropertyException(owningClass, MaxConstraint.VALIDATION_DSL_NAME);
        }

        Range r = getRange();
        if (r != null) {
            LOG.warn("Range constraint already set ignoring constraint [" + MaxConstraint.VALIDATION_DSL_NAME + "] for value [" + max + "]");
            return;
        }

        Constraint c = appliedConstraints.get(MaxConstraint.VALIDATION_DSL_NAME);
        if (c != null) {
            c.setParameter(max);
        } else {
            c = new MaxConstraint();
            c.setOwningClass(owningClass);
            c.setPropertyName(propertyName);
            c.setParameter(max);
            appliedConstraints.put(MaxConstraint.VALIDATION_DSL_NAME, c);
        }
    }

    /**
     * @return Returns the min.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Comparable getMin() {
        Comparable minValue = null;

        MinConstraint minConstraint = (MinConstraint) appliedConstraints.get(MinConstraint.VALIDATION_DSL_NAME);
        RangeConstraint rangeConstraint = (RangeConstraint) appliedConstraints.get(RangeConstraint.VALIDATION_DSL_NAME);

        if ((minConstraint != null) || (rangeConstraint != null)) {
            Comparable minConstraintValue = minConstraint != null ? minConstraint.getMinValue() : null;
            Comparable rangeConstraintLowValue = rangeConstraint != null ? rangeConstraint.getRange().getFrom() : null;

            if ((minConstraintValue != null) && (rangeConstraintLowValue != null)) {
                minValue = (minConstraintValue.compareTo(rangeConstraintLowValue) > 0) ? minConstraintValue : rangeConstraintLowValue;
            } else if ((minConstraintValue == null) && (rangeConstraintLowValue != null)) {
                minValue = rangeConstraintLowValue;
            } else if ((minConstraintValue != null) && (rangeConstraintLowValue == null)) {
                minValue = minConstraintValue;
            }
        }

        return minValue;
    }

    /**
     * @param min The min to set.
     */
    @SuppressWarnings("rawtypes")
    public void setMin(Comparable min) {
        if (min == null) {
            appliedConstraints.remove(MinConstraint.VALIDATION_DSL_NAME);
            return;
        }

        if (!propertyType.equals(min.getClass())) {
            throw new PropertyException(owningClass, MinConstraint.VALIDATION_DSL_NAME);
        }

        Range r = getRange();
        if (r != null) {
            LOG.warn("Range constraint already set ignoring constraint [" + MinConstraint.VALIDATION_DSL_NAME + "] for value [" + min + "]");
            return;
        }

        Constraint c = appliedConstraints.get(MinConstraint.VALIDATION_DSL_NAME);
        if (c != null) {
            c.setParameter(min);
        } else {
            c = new MinConstraint();
            c.setOwningClass(owningClass);
            c.setPropertyName(propertyName);
            c.setParameter(min);
            appliedConstraints.put(MinConstraint.VALIDATION_DSL_NAME, c);
        }
    }

    /**
     * @return Returns the inList.
     */
    @SuppressWarnings("rawtypes")
    public List getInList() {
        InListConstraint c = (InListConstraint) appliedConstraints.get(InListConstraint.VALIDATION_DSL_NAME);
        return c == null ? null : c.getList();
    }

    /**
     * @param inList The inList to set.
     */
    @SuppressWarnings("rawtypes")
    public void setInList(List inList) {
        Constraint c = appliedConstraints.get(InListConstraint.VALIDATION_DSL_NAME);
        if (inList == null) {
            appliedConstraints.remove(InListConstraint.VALIDATION_DSL_NAME);
        } else {
            if (c != null) {
                c.setParameter(inList);
            } else {
                c = new InListConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(inList);
                appliedConstraints.put(InListConstraint.VALIDATION_DSL_NAME, c);
            }
        }
    }

    /**
     * @return Returns the range.
     */
    @SuppressWarnings("rawtypes")
    public Range getRange() {
        RangeConstraint c = (RangeConstraint) appliedConstraints.get(RangeConstraint.VALIDATION_DSL_NAME);
        return c == null ? null : c.getRange();
    }

    /**
     * @param range The range to set.
     */
    @SuppressWarnings("rawtypes")
    public void setRange(Range range) {
        if (appliedConstraints.containsKey(MaxConstraint.VALIDATION_DSL_NAME)) {
            LOG.warn("Setting range constraint on property [" + propertyName + "] of class [" + owningClass + "] forced removal of max constraint");
            appliedConstraints.remove(MaxConstraint.VALIDATION_DSL_NAME);
        }
        if (appliedConstraints.containsKey(MinConstraint.VALIDATION_DSL_NAME)) {
            LOG.warn("Setting range constraint on property [" + propertyName + "] of class [" + owningClass + "] forced removal of min constraint");
            appliedConstraints.remove(MinConstraint.VALIDATION_DSL_NAME);
        }
        if (range == null) {
            appliedConstraints.remove(RangeConstraint.VALIDATION_DSL_NAME);
        } else {
            Constraint c = appliedConstraints.get(RangeConstraint.VALIDATION_DSL_NAME);
            if (c != null) {
                c.setParameter(range);
            } else {
                c = new RangeConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(range);

                appliedConstraints.put(RangeConstraint.VALIDATION_DSL_NAME, c);
            }
        }
    }

    /**
     * @return The scale, if defined for this property; null, otherwise
     */
    public Integer getScale() {
        ScaleConstraint scaleConstraint = (ScaleConstraint) appliedConstraints.get(ScaleConstraint.VALIDATION_DSL_NAME);
        if (scaleConstraint != null) {
            return scaleConstraint.getScale();
        }

        return null;
    }

    /**
     * @return Returns the size.
     */
    @SuppressWarnings("rawtypes")
    public Range getSize() {
        SizeConstraint c = (SizeConstraint) appliedConstraints.get(SizeConstraint.VALIDATION_DSL_NAME);
        return c == null ? null : c.getRange();
    }

    /**
     * @param size The size to set.
     */
    @SuppressWarnings("rawtypes")
    public void setSize(Range size) {
        Constraint c = appliedConstraints.get(SizeConstraint.VALIDATION_DSL_NAME);
        if (size == null) {
            appliedConstraints.remove(SizeConstraint.VALIDATION_DSL_NAME);
        } else {
            if (c != null) {
                c.setParameter(size);
            } else {
                c = new SizeConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(size);
                appliedConstraints.put(SizeConstraint.VALIDATION_DSL_NAME, c);
            }
        }
    }

    /**
     * @return Returns the blank.
     */
    public boolean isBlank() {
        Object cons = appliedConstraints.get(BlankConstraint.VALIDATION_DSL_NAME);
        return cons == null || (Boolean) ((BlankConstraint) cons).getParameter();
    }

    /**
     * @param blank The blank to set.
     */
    @SuppressWarnings("ConstantConditions")
    public void setBlank(boolean blank) {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, BlankConstraint.VALIDATION_DSL_NAME);
        }

        if (!blank) {
            appliedConstraints.remove(BlankConstraint.VALIDATION_DSL_NAME);
        } else {
            Constraint c = appliedConstraints.get(BlankConstraint.VALIDATION_DSL_NAME);
            if (c != null) {
                c.setParameter(blank);
            } else {
                c = new BlankConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(blank);
                appliedConstraints.put(BlankConstraint.VALIDATION_DSL_NAME, c);
            }
        }
    }

    /**
     * @return Returns the email.
     */
    public boolean isEmail() {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, EmailConstraint.VALIDATION_DSL_NAME);
        }

        return appliedConstraints.containsKey(EmailConstraint.VALIDATION_DSL_NAME);
    }

    /**
     * @param email The email to set.
     */
    @SuppressWarnings("ConstantConditions")
    public void setEmail(boolean email) {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, EmailConstraint.VALIDATION_DSL_NAME);
        }

        Constraint c = appliedConstraints.get(EmailConstraint.VALIDATION_DSL_NAME);
        if (email) {
            if (c != null) {
                c.setParameter(email);
            } else {
                c = new EmailConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(email);
                appliedConstraints.put(EmailConstraint.VALIDATION_DSL_NAME, c);
            }
        } else {
            if (c != null) {
                appliedConstraints.remove(EmailConstraint.VALIDATION_DSL_NAME);
            }
        }
    }

    private boolean isNotValidStringType() {
        return !CharSequence.class.isAssignableFrom(propertyType);
    }

    /**
     * @return Returns the creditCard.
     */
    public boolean isCreditCard() {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, CreditCardConstraint.VALIDATION_DSL_NAME);
        }

        return appliedConstraints.containsKey(CreditCardConstraint.VALIDATION_DSL_NAME);
    }

    /**
     * @param creditCard The creditCard to set.
     */
    @SuppressWarnings("ConstantConditions")
    public void setCreditCard(boolean creditCard) {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, CreditCardConstraint.VALIDATION_DSL_NAME);
        }

        Constraint c = appliedConstraints.get(CreditCardConstraint.VALIDATION_DSL_NAME);
        if (creditCard) {
            if (c != null) {
                c.setParameter(creditCard);
            } else {
                c = new CreditCardConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(creditCard);
                appliedConstraints.put(CreditCardConstraint.VALIDATION_DSL_NAME, c);
            }
        } else {
            if (c != null) {
                appliedConstraints.remove(CreditCardConstraint.VALIDATION_DSL_NAME);
            }
        }
    }

    /**
     * @return Returns the matches.
     */
    public String getMatches() {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, MatchesConstraint.VALIDATION_DSL_NAME);
        }
        MatchesConstraint c = (MatchesConstraint) appliedConstraints.get(MatchesConstraint.VALIDATION_DSL_NAME);
        return c == null ? null : c.getRegex();
    }

    /**
     * @param regex The matches to set.
     */
    public void setMatches(String regex) {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, MatchesConstraint.VALIDATION_DSL_NAME);
        }

        Constraint c = appliedConstraints.get(MatchesConstraint.VALIDATION_DSL_NAME);
        if (regex == null) {
            appliedConstraints.remove(MatchesConstraint.VALIDATION_DSL_NAME);
        } else {
            if (c != null) {
                c.setParameter(regex);
            } else {
                c = new MatchesConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(regex);
                appliedConstraints.put(MatchesConstraint.VALIDATION_DSL_NAME, c);
            }
        }
    }

    /**
     * @return Returns the notEqual.
     */
    public Object getNotEqual() {
        NotEqualConstraint c = (NotEqualConstraint) appliedConstraints.get(NotEqualConstraint.VALIDATION_DSL_NAME);
        return c == null ? null : c.getNotEqualTo();
    }

    /**
     * @return Returns the maxSize.
     */
    public Integer getMaxSize() {
        Integer maxSize = null;

        MaxSizeConstraint maxSizeConstraint = (MaxSizeConstraint) appliedConstraints.get(MaxSizeConstraint.VALIDATION_DSL_NAME);
        SizeConstraint sizeConstraint = (SizeConstraint) appliedConstraints.get(SizeConstraint.VALIDATION_DSL_NAME);

        if ((maxSizeConstraint != null) || (sizeConstraint != null)) {
            int maxSizeConstraintValue = maxSizeConstraint != null ? maxSizeConstraint.getMaxSize() : Integer.MAX_VALUE;
            int sizeConstraintHighValue = sizeConstraint != null ? sizeConstraint.getRange().getTo() : Integer.MAX_VALUE;
            maxSize = Math.min(maxSizeConstraintValue, sizeConstraintHighValue);
        }

        return maxSize;
    }

    /**
     * @param maxSize The maxSize to set.
     */
    public void setMaxSize(Integer maxSize) {
        Constraint c = appliedConstraints.get(MaxSizeConstraint.VALIDATION_DSL_NAME);
        if (c != null) {
            c.setParameter(maxSize);
        } else {
            c = new MaxSizeConstraint();
            c.setOwningClass(owningClass);
            c.setPropertyName(propertyName);
            c.setParameter(maxSize);
            appliedConstraints.put(MaxSizeConstraint.VALIDATION_DSL_NAME, c);
        }
    }

    /**
     * @return Returns the minSize.
     */
    public Integer getMinSize() {
        Integer minSize = null;

        MinSizeConstraint minSizeConstraint = (MinSizeConstraint) appliedConstraints.get(MinSizeConstraint.VALIDATION_DSL_NAME);
        SizeConstraint sizeConstraint = (SizeConstraint) appliedConstraints.get(SizeConstraint.VALIDATION_DSL_NAME);

        if ((minSizeConstraint != null) || (sizeConstraint != null)) {
            int minSizeConstraintValue = minSizeConstraint != null ? minSizeConstraint.getMinSize() : Integer.MIN_VALUE;
            int sizeConstraintLowValue = sizeConstraint != null ? sizeConstraint.getRange().getFrom() : Integer.MIN_VALUE;

            minSize = Math.max(minSizeConstraintValue, sizeConstraintLowValue);
        }

        return minSize;
    }

    /**
     * @param minSize The minLength to set.
     */
    public void setMinSize(Integer minSize) {
        Constraint c = appliedConstraints.get(MinSizeConstraint.VALIDATION_DSL_NAME);
        if (c != null) {
            c.setParameter(minSize);
        } else {
            c = new MinSizeConstraint();
            c.setOwningClass(owningClass);
            c.setPropertyName(propertyName);
            c.setParameter(minSize);
            appliedConstraints.put(MinSizeConstraint.VALIDATION_DSL_NAME, c);
        }
    }

    /**
     * @param notEqual The notEqual to set.
     */
    public void setNotEqual(Object notEqual) {
        if (notEqual == null) {
            appliedConstraints.remove(NotEqualConstraint.VALIDATION_DSL_NAME);
        } else {
            Constraint c = new NotEqualConstraint();
            c.setOwningClass(owningClass);
            c.setPropertyName(propertyName);
            c.setParameter(notEqual);
            appliedConstraints.put(NotEqualConstraint.VALIDATION_DSL_NAME, c);
        }
    }

    /**
     * @return Returns the nullable.
     */
    public boolean isNullable() {
        if (appliedConstraints.containsKey(NullableConstraint.VALIDATION_DSL_NAME)) {
            NullableConstraint nc = (NullableConstraint) appliedConstraints.get(NullableConstraint.VALIDATION_DSL_NAME);
            return nc.isNullable();
        }

        return false;
    }

    /**
     * @param nullable The nullable to set.
     */
    public void setNullable(boolean nullable) {
        NullableConstraint nc = (NullableConstraint) appliedConstraints.get(NullableConstraint.VALIDATION_DSL_NAME);
        if (nc == null) {
            nc = new NullableConstraint();
            nc.setOwningClass(owningClass);
            nc.setPropertyName(propertyName);
            appliedConstraints.put(NullableConstraint.VALIDATION_DSL_NAME, nc);
        }

        nc.setParameter(Boolean.valueOf(nullable));
    }

    public Class<?> getOwningClass() {
        return owningClass;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName The propertyName to set.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @return Returns the url.
     */
    public boolean isUrl() {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, UrlConstraint.VALIDATION_DSL_NAME);
        }
        return appliedConstraints.containsKey(UrlConstraint.VALIDATION_DSL_NAME);
    }

    /**
     * @param url The url to set.
     */
    @SuppressWarnings("ConstantConditions")
    public void setUrl(boolean url) {
        if (isNotValidStringType()) {
            throw new PropertyException(owningClass, UrlConstraint.VALIDATION_DSL_NAME);
        }

        Constraint c = appliedConstraints.get(UrlConstraint.VALIDATION_DSL_NAME);
        if (url) {
            if (c != null) {
                c.setParameter(url);
            } else {
                c = new UrlConstraint();
                c.setOwningClass(owningClass);
                c.setPropertyName(propertyName);
                c.setParameter(url);
                appliedConstraints.put(UrlConstraint.VALIDATION_DSL_NAME, c);
            }
        } else {
            if (c != null) {
                appliedConstraints.remove(UrlConstraint.VALIDATION_DSL_NAME);
            }
        }
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * The message source used to evaluate error messages
     *
     * @param source The MessageSource instance to use to resolve messages
     */
    public void setMessageSource(MessageSource source) {
        messageSource = source;
    }

    /**
     * Validate this constrained property against specified property value
     *
     * @param target        The target object to validate
     * @param propertyValue The value of the property to validate
     * @param errors        The Errors instances to report errors to
     */
    public void validate(Object target, Object propertyValue, Errors errors) {
        List<Constraint> delayedConstraints = new ArrayList<Constraint>();

        // validate only vetoing constraints first, putting non-vetoing into delayedConstraints
        for (Constraint c : appliedConstraints.values()) {
            if (c instanceof VetoingConstraint) {
                c.setMessageSource(messageSource);
                // stop validation process when constraint vetoes
                if (((VetoingConstraint) c).validateWithVetoing(target, propertyValue, errors)) {
                    return;
                }
            } else {
                delayedConstraints.add(c);
            }
        }

        // process non-vetoing constraints
        for (Constraint c : delayedConstraints) {
            c.setMessageSource(messageSource);
            c.validate(target, propertyValue, errors);
        }
    }

    /**
     * Checks with this ConstrainedProperty instance supports applying the specified constraint.
     *
     * @param constraintName The name of the constraint
     * @return True if the constraint is supported
     */
    public boolean supportsContraint(String constraintName) {
        if (!constraints.containsKey(constraintName)) {
            return GriffonClassUtils.isWritable(this, constraintName);
        }

        try {
            Constraint c = instantiateConstraint(constraintName, false);
            return c != null && c.supports(propertyType);
        } catch (Exception e) {
            LOG.error("Exception thrown instantiating constraint [" + constraintName +
                "] to class [" + owningClass + "]", e);
            throw new ConstraintException("Exception thrown instantiating  constraint [" + constraintName +
                "] to class [" + owningClass + "]");
        }
    }

    /**
     * Applies a constraint for the specified name and constraint value.
     *
     * @param constraintName    The name of the constraint
     * @param constrainingValue The constraining value
     * @throws ConstraintException Thrown when the specified constraint is not supported by this ConstrainedProperty. Use <code>supportsContraint(String constraintName)</code> to check before calling
     */
    public void applyConstraint(String constraintName, Object constrainingValue) {
        if (constraints.containsKey(constraintName)) {
            if (constrainingValue == null) {
                appliedConstraints.remove(constraintName);
            } else {
                try {
                    Constraint c = instantiateConstraint(constraintName, true);
                    if (c != null) {
                        c.setParameter(constrainingValue);
                        appliedConstraints.put(constraintName, c);
                    }
                } catch (Exception e) {
                    LOG.error("Exception thrown applying constraint [" + constraintName +
                        "] to class [" + owningClass + "] for value [" + constrainingValue + "]: " + e.getMessage(), e);
                    throw new ConstraintException("Exception thrown applying constraint [" + constraintName +
                        "] to class [" + owningClass + "] for value [" + constrainingValue + "]: " + e.getMessage(), e);
                }
            }
        } else if (GriffonClassUtils.isWritable(this, constraintName)) {
            GriffonClassUtils.setPropertyValue(this, constraintName, constrainingValue);
        } else {
            throw new ConstraintException("Constraint [" + constraintName + "] is not supported for property [" +
                propertyName + "] of class [" + owningClass + "] with type [" + propertyType + "]");
        }
    }

    private Constraint instantiateConstraint(String constraintName, boolean validate) throws InstantiationException, IllegalAccessException {
        List<Object> candidateConstraints = constraints.get(constraintName);

        for (Object constraintFactory : candidateConstraints) {

            Constraint c;
            if (constraintFactory instanceof ConstraintFactory) {
                c = ((ConstraintFactory) constraintFactory).newInstance();
            } else {
                c = (Constraint) ((Class<?>) constraintFactory).newInstance();
            }

            c.setOwningClass(owningClass);
            c.setPropertyName(propertyName);

            if (validate && c.isValid()) {
                return c;
            } else if (!validate) {
                return c;
            }

        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("{owningClass=").append(owningClass);
        sb.append(", propertyName='").append(propertyName).append('\'');
        sb.append(", propertyType=").append(propertyType);
        sb.append(", appliedConstraints=").append(appliedConstraints);
        sb.append(", metaConstraints=").append(metaConstraints);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Adds a meta constraints which is a non-validating informational constraint.
     *
     * @param name  The name of the constraint
     * @param value The value
     */
    public void addMetaConstraint(String name, Object value) {
        metaConstraints.put(name, value);
    }

    /**
     * Obtains the value of the named meta constraint.
     *
     * @param name The name of the constraint
     * @return The value
     */
    public Object getMetaConstraintValue(String name) {
        return metaConstraints.get(name);
    }

    /**
     * @return Returns the display.
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * @param display The display to set.
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * @return Returns the editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * @param editable The editable to set.
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public String getWidget() {
        return widget;
    }

    public void setWidget(String widget) {
        this.widget = widget;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
