/*
 * Copyright 2016-2017 the original author or authors.
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
package org.example.api;

import retrofit2.Response;
import rx.Observable;

import javax.annotation.Nonnull;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class ObservableUtils {
    private ObservableUtils() {
        // prevent instantiation
    }

    @Nonnull
    public static <T> Observable<T> paginatedObservable(@Nonnull final FirstPageSupplier<T> firstPage, @Nonnull final NextPageSupplier<T> nextPage) {
        requireNonNull(firstPage, "Argument 'firstPage' must not be null");
        requireNonNull(nextPage, "Argument 'nextPage' must not be null");

        return processPage(nextPage, firstPage.get());
    }

    private static <T> Observable<T> processPage(@Nonnull final NextPageSupplier<T> supplier, @Nonnull Observable<Response<List<T>>> items) {
        return items.flatMap(response -> {
            if (response.isSuccessful()) {
                Links links = Links.of(response.headers().get("Link"));
                Observable<T> currentPage = Observable.from(response.body());
                if (links.hasNext()) {
                    return currentPage.concatWith(processPage(supplier, supplier.get(links)));
                }
                return currentPage;
            }
            return Observable.error(new HttpResponseException(response.code(), response.message()));
        });
    }

    public interface FirstPageSupplier<T> {
        @Nonnull
        Observable<Response<List<T>>> get();
    }

    public interface NextPageSupplier<T> {
        @Nonnull
        Observable<Response<List<T>>> get(@Nonnull Links links);
    }
}