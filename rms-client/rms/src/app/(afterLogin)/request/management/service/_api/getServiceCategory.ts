import {Paging} from "@/model/Paging";

export async function getServiceCategory(search: Paging) {
    return await fetch(`/w-api/management-service/service_category`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(search),
        credentials: 'include',
        cache: 'no-store'
    });
}