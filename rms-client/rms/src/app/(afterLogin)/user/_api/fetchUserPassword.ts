export async function fetchUserPassword(userId: string, newPassword: string, confirmPassword: string) {
    const res = await fetch(`/w-api/management-service/users/${userId}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            id: userId,
            password: {
                newPassword: newPassword,
                confirmPassword: confirmPassword,
            }
        }),
        credentials: 'include',
        cache: 'no-store'
    });

    const text = await res.text();
    return text ? JSON.parse(text) : {};
}