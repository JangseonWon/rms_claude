export async function getServices(userId: string) {
    return await fetch(`/w-api/management-service/services/users/${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}