package com.example.Rider.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ERole {
    ROLE_SUPER_ADMIN("Super Administrator"),
    ROLE_ADMIN("Administrator"),
    ROLE_SUPERVISOR("Supervisor"),
    ROLE_OPERATOR("Operator");

    private final String displayName;

    ERole(String displayName) {
        this.displayName = displayName;
    }

    public static ERole getEnumByString(String code) {
        for (ERole item : ERole.values()) {
            if (code.equals(item.displayName)) {
                return item;
            }
        }
        return null;
    }

    public static ERole[] getSortedValue() {
        ERole[] values = ERole.values();
        Arrays.sort(values, (s1, s2) -> s1.getDisplayName().compareTo(s2.getDisplayName()));
        return values;
    }
}
