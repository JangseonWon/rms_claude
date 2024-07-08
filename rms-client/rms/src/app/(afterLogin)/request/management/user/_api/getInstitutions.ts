export async function getInstitutions(userId: string) {
    return await fetch(`/w-api/management-service/users/${userId}/organizations`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}