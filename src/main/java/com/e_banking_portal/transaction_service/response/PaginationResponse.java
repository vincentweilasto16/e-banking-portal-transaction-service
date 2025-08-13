package com.e_banking_portal.transaction_service.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PaginationResponse<T> {

 @JsonProperty("data")
 private T data;

 @JsonProperty("page_number")
 private int pageNumber;

 @JsonProperty("page_size")
 private int pageSize;

 @JsonProperty("total_elements")
 private long totalElements;

 @JsonProperty("total_pages")
 private int totalPages;

 public PaginationResponse(TransactionResponse data, int pageNumber, int pageSize, long totalElements) {
  this.data = (T) data;
  this.pageNumber = pageNumber;
  this.pageSize = pageSize;
  this.totalElements = totalElements;
  this.totalPages = totalPages;
 }

 public T getData() {
  return data;
 }

 public void setData(T data) {
  this.data = data;
 }

 public int getPageNumber() {
  return pageNumber;
 }

 public void setPageNumber(int pageNumber) {
  this.pageNumber = pageNumber;
 }

 public int getPageSize() {
  return pageSize;
 }

 public void setPageSize(int pageSize) {
  this.pageSize = pageSize;
 }

 public long getTotalElements() {
  return totalElements;
 }

 public void setTotalElements(long totalElements) {
  this.totalElements = totalElements;
 }

 public int getTotalPages() {
  return totalPages;
 }

 public void setTotalPages(int totalPages) {
  this.totalPages = totalPages;
 }
}
