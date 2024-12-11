export async function getOrganization(userId: string) {
    const res = await fetch(`/w-api/cart-service/organizations?user_id=${userId}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}