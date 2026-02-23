package com.velvet.rest.common.encryption;

import java.io.Serializable;

import lombok.Data;

@Data
public class EncryptedPayload implements Serializable {

	private static final long serialVersionUID = 7605600666638973288L;

	private String data;

}
