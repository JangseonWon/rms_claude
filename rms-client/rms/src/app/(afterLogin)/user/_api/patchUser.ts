import {User} from "@/model/User";

export async function patchUser (user: User) {
    return await fetch(`/w-api/profile-service/users/${user.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
        credentials: 'include',
        cache: 'no-store'
    });
}