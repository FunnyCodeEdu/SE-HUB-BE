package com.se.hub.modules.payment.service;

import com.se.hub.modules.payment.dto.request.GenerateQRRequest;
import com.se.hub.modules.payment.dto.response.QRCodeResponse;

public interface PaymentManagementService {
    QRCodeResponse generateQRCode(GenerateQRRequest request);
}

