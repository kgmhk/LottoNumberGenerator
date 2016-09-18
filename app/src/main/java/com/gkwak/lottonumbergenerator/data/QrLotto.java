package com.gkwak.lottonumbergenerator.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gkwak on 9/18/16.
 */
public class QrLotto {
    List<String> qrNumber;

    public QrLotto(List<String> qrNumber) {
        this.qrNumber = qrNumber;
    }

    public List<String> getQrNumber() {
        return qrNumber;
    }

}
