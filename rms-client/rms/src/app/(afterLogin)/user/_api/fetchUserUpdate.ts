import {User} from "@/model/User";

export async function fetchUserUpdate(user: User, userId: string | undefined) {
    const res = await fetch(`/w-api/management-service/users/${userId}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) throw new Error('Failed to fetch data');

    return await res.json();
}