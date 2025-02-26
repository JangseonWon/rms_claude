package com.gcgenome.rms.exceptions

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException

class AuthenticationNotFoundException(): AuthenticationCredentialsNotFoundException("Access denied. Authentication failed or insufficient permissions.") {
}