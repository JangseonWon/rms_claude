import {User} from "@/model/User";

export async function addManager(user: User | undefined) {
    return await fetch(`/w-api/management-service/user`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
        credentials: 'include',
        cache: 'no-store'
    });
}