-- liquibase formatted sql

-- changeset dmitriyBreyfogel:034-add-permission-resources

-- Me API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CREATE_OWN_SPECIALIST' AND r.url_pattern = '/api/v1/me/specialist' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CREATE_OWN_ORGANIZATION' AND r.url_pattern = '/api/v1/me/organizations' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_OWN_ORGANIZATIONS' AND r.url_pattern = '/api/v1/me/organizations' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_OWN_ORGANIZATION' AND r.url_pattern = '/api/v1/me/organizations/{organizationId}' AND r.http_method = 'DELETE';

-- Slots API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CREATE_SLOT' AND r.url_pattern = '/api/v1/slots' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_SLOT' AND r.url_pattern = '/api/v1/slots/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_SLOTS' AND r.url_pattern = '/api/v1/slots' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_ANY_SLOTS' AND r.url_pattern = '/api/v1/slots/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'DELETE_ALL_SLOTS' AND r.url_pattern = '/api/v1/slots' AND r.http_method = 'DELETE';

-- Slot Booking Requests API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CREATE_BOOKING' AND r.url_pattern = '/api/v1/slot-booking-requests' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_BOOKINGS' AND r.url_pattern = '/api/v1/slot-booking-requests/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_OWN_BOOKINGS' AND r.url_pattern = '/api/v1/slot-booking-requests/me' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_BOOKINGS' AND r.url_pattern = '/api/v1/slot-booking-requests/incoming' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_BOOKINGS' AND r.url_pattern = '/api/v1/slot-booking-requests/slots/{slotId}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'ACCEPT_BOOKING' AND r.url_pattern = '/api/v1/slot-booking-requests/{id}/accept' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'REJECT_BOOKING' AND r.url_pattern = '/api/v1/slot-booking-requests/{id}/reject' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CANCEL_OWN_BOOKING' AND r.url_pattern = '/api/v1/slot-booking-requests/{id}/cancel' AND r.http_method = 'POST';

-- Organizations API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CREATE_ANY_ORGANIZATION' AND r.url_pattern = '/api/v1/organizations' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'ADD_SPECIALIST_TO_ORG' AND r.url_pattern = '/api/v1/organizations/{organizationId}/specialists/{specialistUserId}' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_ORGANIZATION' AND r.url_pattern = '/api/v1/organizations/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ORGANIZATIONS_FOR_BOOKING' AND r.url_pattern = '/api/v1/organizations' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ORG_SPECIALISTS' AND r.url_pattern = '/api/v1/organizations/{organizationId}/specialists' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_ANY_ORGANIZATION' AND r.url_pattern = '/api/v1/organizations/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_ANY_ORGANIZATION' AND r.url_pattern = '/api/v1/organizations/{organizationId}/specialists/{specialistUserId}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'DELETE_ALL_ORGANIZATIONS' AND r.url_pattern = '/api/v1/organizations' AND r.http_method = 'DELETE';

-- Specialists API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'CREATE_ANY_SPECIALIST' AND r.url_pattern = '/api/v1/specialists' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_SPECIALIST' AND r.url_pattern = '/api/v1/specialists/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_SPECIALIST_ORGANIZATIONS' AND r.url_pattern = '/api/v1/specialists/{specialistUserId}/organizations' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ANY_SPECIALIST' AND r.url_pattern = '/api/v1/specialists' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'DELETE_ANY_SPECIALIST' AND r.url_pattern = '/api/v1/specialists/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'DELETE_ALL_SPECIALISTS' AND r.url_pattern = '/api/v1/specialists' AND r.http_method = 'DELETE';

-- Users API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_USERS' AND r.url_pattern = '/api/v1/users/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_USERS' AND r.url_pattern = '/api/v1/users' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_USERS' AND r.url_pattern = '/api/v1/users/by-email' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_USERS' AND r.url_pattern = '/api/v1/users/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_USERS' AND r.url_pattern = '/api/v1/users' AND r.http_method = 'DELETE';

-- Roles API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_ROLES' AND r.url_pattern = '/api/v1/roles' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ROLES' AND r.url_pattern = '/api/v1/roles/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ROLES' AND r.url_pattern = '/api/v1/roles/by-name' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_ROLES' AND r.url_pattern = '/api/v1/roles' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_ROLES' AND r.url_pattern = '/api/v1/roles/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_ROLES' AND r.url_pattern = '/api/v1/roles' AND r.http_method = 'DELETE';

-- Permissions API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_PERMISSIONS' AND r.url_pattern = '/api/v1/permissions' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_PERMISSIONS' AND r.url_pattern = '/api/v1/permissions/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_PERMISSIONS' AND r.url_pattern = '/api/v1/permissions/by-code' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_PERMISSIONS' AND r.url_pattern = '/api/v1/permissions' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_PERMISSIONS' AND r.url_pattern = '/api/v1/permissions/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_PERMISSIONS' AND r.url_pattern = '/api/v1/permissions' AND r.http_method = 'DELETE';

-- Resources API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_RESOURCES' AND r.url_pattern = '/api/v1/resources' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_RESOURCES' AND r.url_pattern = '/api/v1/resources/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_RESOURCES' AND r.url_pattern = '/api/v1/resources' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_RESOURCES' AND r.url_pattern = '/api/v1/resources/{id}' AND r.http_method = 'DELETE';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'MANAGE_RESOURCES' AND r.url_pattern = '/api/v1/resources' AND r.http_method = 'DELETE';

-- Refresh Tokens API
INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/{id}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/by-hash/{hash}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/by-user' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/count' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'VIEW_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/count/{revoked}' AND r.http_method = 'GET';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'REVOKE_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/revoke/{hash}' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'REVOKE_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/users/{userId}/revoke' AND r.http_method = 'POST';

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id FROM permissions p, resources r
WHERE p.code = 'REVOKE_REFRESH_TOKENS' AND r.url_pattern = '/api/v1/refreshTokens/cleanup' AND r.http_method = 'DELETE';