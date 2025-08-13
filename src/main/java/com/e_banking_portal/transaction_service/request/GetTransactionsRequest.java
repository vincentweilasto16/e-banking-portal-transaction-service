/**
 * aniboys.id
 * Copyright (c) 2017-2025 All Rights Reserved.
 */
package com.e_banking_portal.transaction_service.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionsRequest {
    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Month must be in format YYYY-MM")
    private String month;

    @Min(0)
    private int page = 0;

    @Min(1)
    private int size = 20;
}