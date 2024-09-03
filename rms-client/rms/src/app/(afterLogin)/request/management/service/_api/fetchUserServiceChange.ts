export async function fetchUserServiceChange(serviceId: string, state: boolean) {
    const method = state ? 'PATCH' : 'DELETE';

    return await fetch(`/w-api/management-service/users/services/${serviceId}`, {
        method: method,
        headers: {
            "Content-Type": "application/json",
        },
        credentials: 'include',
        cache: 'no-store'
    });
}