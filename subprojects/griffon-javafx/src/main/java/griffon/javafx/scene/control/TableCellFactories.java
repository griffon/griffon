/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package griffon.javafx.scene.control;

import griffon.annotations.core.Nonnull;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class TableCellFactories {
    private static final String ERROR_ITEMS_NULL = "Argument 'items' must not be null";
    private static final String ERROR_ENUMTYPE_NULL = "Argument 'enumType' must not be null";

    @Nonnull
    public static <E, T> TableCellFactory<E, T> checkboxTableCellFactory() {
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                CheckBoxTableCell<E, T> cell = new CheckBoxTableCell<>();
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableCheckboxTableCellFactory() {
        return new EditableTableCellFactory<E, T>(checkboxTableCellFactory());
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> choiceBoxTableCellFactory(@Nonnull E... items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        final ObservableList<E> observableItems = observableArrayList(items);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ChoiceBoxTableCell<E, T> cell = new ChoiceBoxTableCell<>((ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> choiceBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull E... items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        final ObservableList<E> observableItems = observableArrayList(items);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ChoiceBoxTableCell<E, T> cell = new ChoiceBoxTableCell<>((StringConverter<T>) converter, (ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> choiceBoxTableCellFactory(@Nonnull final ObservableList<E> observableItems) {
        requireNonNull(observableItems, ERROR_ITEMS_NULL);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ChoiceBoxTableCell<E, T> cell = new ChoiceBoxTableCell<>((ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> choiceBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull final ObservableList<E> observableItems) {
        requireNonNull(observableItems, ERROR_ITEMS_NULL);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ChoiceBoxTableCell<E, T> cell = new ChoiceBoxTableCell<>((StringConverter<T>) converter, (ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> choiceBoxTableCellFactory(@Nonnull Class<E> enumType) {
        requireNonNull(enumType, ERROR_ENUMTYPE_NULL);
        final ObservableList<E> observableItems = observableArrayList(enumType.getEnumConstants());
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ChoiceBoxTableCell<E, T> cell = new ChoiceBoxTableCell<>((ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> choiceBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull Class<E> enumType) {
        requireNonNull(enumType, ERROR_ENUMTYPE_NULL);
        final ObservableList<E> observableItems = observableArrayList(enumType.getEnumConstants());
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ChoiceBoxTableCell<E, T> cell = new ChoiceBoxTableCell<>((StringConverter<T>) converter, (ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableChoiceBoxTableCellFactory(@Nonnull E... items) {
        return new EditableTableCellFactory<E, T>(choiceBoxTableCellFactory(items));
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableChoiceBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull E... items) {
        return new EditableTableCellFactory<E, T>(choiceBoxTableCellFactory(converter, items));
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableChoiceBoxTableCellFactory(@Nonnull final ObservableList<E> observableItems) {
        return new EditableTableCellFactory<E, T>(choiceBoxTableCellFactory(observableItems));
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableChoiceBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull final ObservableList<E> observableItems) {
        return new EditableTableCellFactory<E, T>(choiceBoxTableCellFactory(converter, observableItems));
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> editableChoiceBoxTableCellFactory(@Nonnull Class<E> enumType) {
        return new EditableTableCellFactory<E, T>(choiceBoxTableCellFactory(enumType));
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> editableChoiceBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull Class<E> enumType) {
        return new EditableTableCellFactory<E, T>(choiceBoxTableCellFactory(converter, enumType));
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> comboBoxTableCellFactory(@Nonnull E... items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        final ObservableList<E> observableItems = observableArrayList(items);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ComboBoxTableCell<E, T> cell = new ComboBoxTableCell<>((ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> comboBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull E... items) {
        requireNonNull(items, ERROR_ITEMS_NULL);
        final ObservableList<E> observableItems = observableArrayList(items);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ComboBoxTableCell<E, T> cell = new ComboBoxTableCell<>((StringConverter<T>) converter, (ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> comboBoxTableCellFactory(@Nonnull final ObservableList<E> observableItems) {
        requireNonNull(observableItems, ERROR_ITEMS_NULL);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ComboBoxTableCell<E, T> cell = new ComboBoxTableCell<>((ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> comboBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull final ObservableList<E> observableItems) {
        requireNonNull(observableItems, ERROR_ITEMS_NULL);
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ComboBoxTableCell<E, T> cell = new ComboBoxTableCell<>((StringConverter<T>) converter, (ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> comboBoxTableCellFactory(@Nonnull Class<E> enumType) {
        requireNonNull(enumType, ERROR_ENUMTYPE_NULL);
        final ObservableList<E> observableItems = observableArrayList(enumType.getEnumConstants());
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ComboBoxTableCell<E, T> cell = new ComboBoxTableCell<>((ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> comboBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull Class<E> enumType) {
        requireNonNull(enumType, ERROR_ENUMTYPE_NULL);
        final ObservableList<E> observableItems = observableArrayList(enumType.getEnumConstants());
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                ComboBoxTableCell<E, T> cell = new ComboBoxTableCell<>((StringConverter<T>) converter, (ObservableList<T>) observableItems);
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableComboBoxTableCellFactory(@Nonnull E... items) {
        return new EditableTableCellFactory<E, T>(comboBoxTableCellFactory(items));
    }

    @SafeVarargs
    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableComboBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull E... items) {
        return new EditableTableCellFactory<E, T>(comboBoxTableCellFactory(converter, items));
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableComboBoxTableCellFactory(@Nonnull final ObservableList<E> observableItems) {
        return new EditableTableCellFactory<E, T>(comboBoxTableCellFactory(observableItems));
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableComboBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull final ObservableList<E> observableItems) {
        return new EditableTableCellFactory<E, T>(comboBoxTableCellFactory(converter, observableItems));
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> editableComboBoxTableCellFactory(@Nonnull Class<E> enumType) {
        return new EditableTableCellFactory<E, T>(comboBoxTableCellFactory(enumType));
    }

    @Nonnull
    public static <E extends Enum, T> TableCellFactory<E, T> editableComboBoxTableCellFactory(@Nonnull final StringConverter<E> converter, @Nonnull Class<E> enumType) {
        return new EditableTableCellFactory<E, T>(comboBoxTableCellFactory(converter, enumType));
    }

    @Nonnull
    public static <E> TableCellFactory<E, Double> progressBarTableCellFactory() {
        return new TableCellFactory<E, Double>() {
            @Nonnull
            @Override
            public TableCell<E, Double> createTableCell(@Nonnull TableColumn<E, Double> tableColumn) {
                return new ProgressBarTableCell<>();
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> textFieldTableCellFactory() {
        return new TableCellFactory<E, T>() {
            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
                TextFieldTableCell<E, T> cell = new TextFieldTableCell<>();
                cell.setEditable(false);
                return cell;
            }
        };
    }

    @Nonnull
    public static <E, T> TableCellFactory<E, T> editableTextFieldTableCellFactory() {
        return new EditableTableCellFactory<E, T>(textFieldTableCellFactory());
    }

    private static class EditableTableCellFactory<E, T> implements TableCellFactory<E, T> {
        private final TableCellFactory delegate;

        private EditableTableCellFactory(@Nonnull TableCellFactory<E, T> delegate) {
            this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
        }

        @Nonnull
        @Override
        public TableCell<E, T> createTableCell(@Nonnull TableColumn<E, T> tableColumn) {
            TableCell<E, T> cell = delegate.createTableCell(tableColumn);
            cell.setEditable(true);
            return cell;
        }
    }
}
