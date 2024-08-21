import {Filter} from "@/model/Filter";

export async function getServices(search: Filter) {
    return await fetch(`/w-api/management-service/services`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}