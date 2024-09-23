import {Query} from "@/model/Query";

export async function getUserWithServices(userId: string, query?: Query) {
    return await fetch(`/w-api/management-service/users/${userId}/services`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: query ? JSON.stringify(query) : undefined,
        credentials: 'include',
        cache: 'no-store'
    });
}