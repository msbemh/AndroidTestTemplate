package com.example.test.repository;

public interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}