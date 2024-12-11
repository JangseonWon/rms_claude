import {Organization} from "@/model/Organization";

export async function patchOrganization(userId: string, organization: Organization) {
    const res = await fetch(`/w-api/profile-service/users/${userId}/organizations/${organization.id}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(organization),
        credentials: 'include',
        cache: 'no-store'
    });
    if (!res.ok) {
        const errorMessage = await res.text();
        // alert(`Error: ${errorMessage}`);
    }
    return res
}