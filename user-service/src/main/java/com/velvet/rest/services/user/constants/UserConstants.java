package com.velvet.rest.services.user.constants;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

public class UserConstants {

	public enum Gender {

		MALE(0), FEMALE(1), OTHER(2);

		private int value;

		private Gender(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static Gender getGender(int value) {
			for (final Gender status : Gender.values()) {
				if (status.getValue() == value) {
					return status;
				}
			}
			return null;
		}

		public static boolean isValidId(int value) {
			for (final Gender orderStatus : Gender.values()) {
				if (orderStatus.getValue() == value) {
					return true;
				}
			}
			return false;
		}
	}

	public enum DeviceType {

		ANDROID("android"), IOS("ios"), WEB("web");

		private final String text;

		DeviceType(String text) {
			this.text = text;
		}

		@JsonCreator
		public static DeviceType fromText(String text) {
			for (DeviceType deviceType : DeviceType.values()) {
				if (deviceType.getText().equals(text)) {
					return deviceType;
				}
			}
			throw new IllegalArgumentException();
		}

		public String getText() {
			return this.text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public enum OtpType {
		EMAIL, PHONE
	}

	public enum ProviderType {
		GOOGLE, APPLE
	}

	public static final String AUTH_PROVIDER_TABLE_NAME = "auth_providers";

}