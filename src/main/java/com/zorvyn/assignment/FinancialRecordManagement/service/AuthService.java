package com.zorvyn.assignment.FinancialRecordManagement.service;


import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginRequestDto;
import com.zorvyn.assignment.FinancialRecordManagement.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDto loginRequest);
}
