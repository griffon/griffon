package griffon.javafx

import griffon.javafx.collections.GriffonFXCollections
import griffon.javafx.collections.MappingObservableList
import groovy.transform.CompileStatic
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.collections.ObservableSet

import javax.annotation.Nonnull
import java.util.function.Function

/**
 * @author Andres Almiray
 * @since 2.13.0-SNAPSHOT
 */
@CompileStatic
class GriffonFXCollectionsExtension {
    @Nonnull
    static <T> ObservableList<T> uiThreadAware(@Nonnull ObservableList<T> self) {
        return GriffonFXCollections.uiThreadAwareObservableList(self)
    }

    @Nonnull
    static <T> ObservableSet<T> uiThreadAware(@Nonnull ObservableSet<T> self) {
        return GriffonFXCollections.uiThreadAwareObservableSet(self)
    }

    @Nonnull
    static <K, V> ObservableMap<K, V> uiThreadAware(@Nonnull ObservableMap<K, V> self) {
        return GriffonFXCollections.uiThreadAwareObservableMap(self)
    }

    @Nonnull
    static <T, S> ObservableList<T> mappedAs(@Nonnull ObservableList<S> self, @Nonnull Function<S, T> mapper) {
        new MappingObservableList<T, S>(self, mapper)
    }


    @Nonnull
    static <T, S> ObservableList<T> mappedAs(
        @Nonnull ObservableList<S> self, @Nonnull ObservableValue<Function<S, T>> mapper) {
        new MappingObservableList<T, S>(self, mapper)
    }
}
