export async function putUserService(userId: string, serviceId: string) {
    return await fetch(`/w-api/management-service/users/${userId}/services/${serviceId}`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
}