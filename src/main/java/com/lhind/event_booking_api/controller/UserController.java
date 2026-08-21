package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.user.ChangePasswordRequest;
import com.lhind.event_booking_api.dto.user.RoleUpdateRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;
import com.lhind.event_booking_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Users",
        description = "Endpoints for user profile management and admin user operations"
)
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    // CURRENT USER

    @Operation(
            summary = "Get current user",
            description = "Returns the profile of the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        return ResponseEntity.ok(
                userService.getCurrentUser()
        );
    }

    @Operation(
            summary = "Update current user",
            description = "Updates the profile information of the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists"
            )
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateCurrentUser(
                        request
                )
        );
    }

    @Operation(
            summary = "Change current user password",
            description = "Changes the password of the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        userService.changePassword(
                request
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    // ADMIN

    @Operation(
            summary = "Get all users",
            description = "Returns all users. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @Operation(
            summary = "Get user by id",
            description = "Returns a user by id. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.getUserById(
                        id
                )
        );
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by id. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @Operation(
            summary = "Update user role",
            description = "Changes the role of a user. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User role updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateUserRole(
                        id,
                        request
                )
        );
    }
}