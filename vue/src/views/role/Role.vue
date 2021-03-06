<template>
    <el-container class="role-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item label="启用">
                    <el-select v-model="queryForm.isEnabled" clearable>
                        <el-option key="已启用" label="已启用" :value="true"></el-option>
                        <el-option key="未启用" label="未启用" :value="false"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="默认添加">
                    <el-select v-model="queryForm.isDefault" clearable>
                        <el-option key="是" label="是" :value="true"></el-option>
                        <el-option key="否" label="否" :value="false"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadRoles" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="() =>{dialogOpened = true;}" size="mini"
                               icon="el-icon-circle-plus">新增
                    </el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="roles" size="mini">
                <el-table-column prop="roleId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否启用">
                    <template slot-scope="scope">
                        {{ scope.row.isEnabled ? "已启用" : "未启用" }}
                    </template>
                </el-table-column>
                <el-table-column label="默认添加">
                    <template slot-scope="scope">
                        {{ scope.row.isDefault ? "是" : "否" }}
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template>
                                <i @click="()=>{toRoleEdit(scope.row.roleId)}" class="el-icon-edit"></i>
                                <i @click="()=>{deleteRole(scope.row.roleId)}" class="el-icon-delete"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-dialog title="新增" :visible.sync="dialogOpened" width="50%" class="edit-dialog"
                   :before-close="preventCloseWhenProcessing">
            <el-form :model="role" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="role.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="role.description"></el-input>
                </el-form-item>
                <el-form-item label="默认添加">
                    <el-switch v-model="role.isDefault" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="role.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
            </el-form>
            <el-footer class="text-right">
                <el-button @click="() => {role = {}; dialogOpened = false;}" :disabled="dialogProcessing">取 消
                </el-button>
                <el-button type="primary" @click="addRole" :loading="dialogProcessing"
                           :disabled="dialogProcessing">确 定
                </el-button>
            </el-footer>
        </el-dialog>

    </el-container>
</template>


<script>
import {mapState, mapActions} from 'vuex';

export default {
    props: {
        clientId: {
            type: Number,
            default: 0
        },
    },

    data() {
        return {
            queryForm: {
                name: '',
                size: 1000,
            },

            roles: [],

            role: {},
            dialogOpened: false,
            dialogProcessing: false,
        }
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.role = this;

        this.loadRoles();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadRoles() {
            this.startProgress();
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role`, {
                params: {...this.queryForm, clientId: this.clientId}
            }).then(response => {
                this.roles = response.data.data;
            }).finally(() => this.stopProgress());
        },

        addRole() {
            this.dialogProcessing = true;
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/role`, {...this.role, clientId: this.clientId}).then(() => {
                this.loadRoles();
                this.dialogOpened = false;
            }).finally(() => this.dialogProcessing = false);
        },

        preventCloseWhenProcessing() {
            if (this.dialogProcessing) {
                return false;
            }

            this.role = {};
            this.dialogOpened = false;
        },

        toRoleEdit(roleId) {
            this.$router.push({
                path: '/role/role-edit',
                query: {roleId: roleId, clientId: this.clientId}
            })
        },

        deleteRole(roleId) {
            this.$confirm('确认删除, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.dialogProcessing = true;
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/role/batch`, {
                    type: this.$constants.batchMethod.DELETE, roleIds: [roleId]
                }).then(() => {
                    this.loadRoles();
                }).finally(() => this.dialogProcessing = false);
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },

    }

}
</script>

<style lang="scss">
.project-page {
    .el-table {
        .cell {
            min-height: 22px;
        }
    }

    .edit-dialog {
        .el-dialog {
            min-width: 500px;
        }
    }
}
</style>
