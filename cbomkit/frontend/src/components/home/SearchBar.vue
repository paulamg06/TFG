<template>
  <div>
    <div class="search">
      <!-- pmg: añadido parámetro a la función connectAndScan para incluir excludedAssets -->
      <cv-search
        :light="true"
        class="search-bar"
        placeholder="Enter Git URL or Package URL to scan"
        v-model="model.codeOrigin.scanUrl"
        @keyup.enter="connectAndScan(advancedOptions()[0], advancedOptions()[1], advancedOptions()[2], advancedOptions()[3])"
      />
      <!-- pmg: añadido parámetro a la función connectAndScan para incluir excludedAssets -->
      <cv-button
        class="search-button"
        :icon="ArrowRight24"
        @click="connectAndScan(advancedOptions()[0], advancedOptions()[1], advancedOptions()[2], advancedOptions()[3])"
        :disabled="!model.codeOrigin.scanUrl"
        >Scan</cv-button
      >
    </div>
    <div style="color: var(--cds-text-secondary)">
      <cv-checkbox
        class="filter-checkbox"
        label="Advanced options"
        v-model="filterOpen"
        value="filter"
      />
    </div>
    <Transition name="filters">
      <div v-show="filterOpen">
        <cv-tabs style="padding-top: 15px; padding-bottom: 10px">
          <!-- pmg: añadida pestaña para introducir activos a excluir por texto -->
          <cv-tab label="Assets">
            <treeselect
              class="filter-input"
              :multiple="true"
              :options="assetList"
              placeholder="Select assets to exclude"
              v-model="value"
              />
            <treeselect-value :value="value"/>
          </cv-tab>
          <cv-tab label="Scan">
            <cv-text-input
              class="filter-input"
              label="Branch"
              placeholder="Specify a specific branch"
              v-model="gitBranch"
            />
            <cv-text-input
              class="filter-input"
              label="Subfolder"
              placeholder="Specify a specific subfolder to scan"
              v-model="gitSubfolder"
            />
          </cv-tab>
          <cv-tab label="Authentication">
            <cv-text-input
                class="filter-input"
                label="Username"
                placeholder="If using an access Token (PAT), leave blank"
                v-model="username"
            />
            <cv-text-input
                type="password"
                class="filter-input"
                label="Password / Access Token (PAT)"
                placeholder="The password for the user or anccess token (PAT) for authentication"
                v-model="passwordOrPAT"
            />
          </cv-tab>
        </cv-tabs>
      </div>
    </Transition>
  </div>
</template>

<script>
import { model } from "@/model.js";
import { connectAndScan } from "@/helpers";
import { ArrowRight24 } from "@carbon/icons-vue";
import { Treeselect } from "@riophae/vue-treeselect";
import '@riophae/vue-treeselect/dist/vue-treeselect.css';

export default {
  name: "SearchBar",
  components: { Treeselect },
  data() {
    return {
      model,
      connectAndScan,
      ArrowRight24,
      filterOpen: false,
      gitBranch: null,
      gitSubfolder: null,
      username: null,
      passwordOrPAT: null,
      assetList: [],
    };
  },

  mounted() {
    this.assetList = [
      {
        id: 'crypto',
        label: 'Criptografía',
        children: [
          { id: 'aes', label: 'AES' },
          { id: 'rsa', label: 'RSA' }
        ]
      },
      {
        id: 'hashing',
        label: 'Hashing',
        children: [
          { id: 'sha256', label: 'SHA-256' },
          { id: 'md5', label: 'MD5' }
        ]
      }
    ];
  },

  methods: {
    advancedOptions: function () {
      // pmg: añadido parámetro para incluir excludedAssets
      if (this.filterOpen) {
        return [this.excludedAssets, this.gitBranch, this.gitSubfolder, { username: this.username, passwordOrPAT: this.passwordOrPAT }];
      } else {
        return [null, null, null, null];
      }
    },

    // pmg
    getAssetList() {
      const rawAssets = ["AES", "RSA", "SHA-256", "MD5"];
      console.log("Adding assets to list: ", rawAssets);
      this.assetList = rawAssets.map(asset => ({ label: asset, value: asset }));
    },
  },
};
</script>

<style scoped>
.search {
  display: flex;
  padding-bottom: 1%;
}
.search-button {
  width: 110px;
}

.search-bar {
  padding-right: 15px;
}
.filter-input {
  padding-top: 10px;
}
.vue-treeselect{
  color: black;
}
/* Transition for advanced options */
.filters-enter-active,
.filters-leave-active {
  transition: all 0.4s;
  /* max-height should be larger than the tallest element: https://stackoverflow.com/questions/42591331/animate-height-on-v-if-in-vuejs-using-transition */
  max-height: 250px;
}
.filters-enter,
.filters-leave-to {
  opacity: 0;
  max-height: 0;
}
</style>
