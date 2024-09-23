export async function deleteUserService(userId: string, serviceId: string) {
    return await fetch(`/w-api/management-service/users/${userId}/services/${serviceId}`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
}