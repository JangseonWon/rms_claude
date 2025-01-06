export async function getOrganization(userId: string) {
    return await fetch(`/w-api/catalog-service/organizations?user_id=${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
}