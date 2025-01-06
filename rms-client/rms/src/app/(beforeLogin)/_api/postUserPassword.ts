import {User} from "@/model/User";

export async function postUserPassword(user: User) {
    return await fetch(`/w-api/login-service/password`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
        credentials: 'include',
        cache: 'no-store'
    });
}